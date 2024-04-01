package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.helpers.TwilioService;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.GuestRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.twilio.Twilio;
import com.twilio.rest.lookups.v2.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GuestRepository guestRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwilioService twilioService;

    @Value("${spring.sendgrid.api-key}")
    private String SENDGRID_API_KEY;

    private static final String VERIFICATION_TEMPLATE_ID = "d-3d2f42ee76ed4904bb916951f3471b95";
    public void userExist(String email, String phoneNumber){

        var userWrapper = userRepository.findByEmail(email);
        if(userWrapper.isPresent()) throw new EntityAlreadyExistsException("User with this email already exists");


    }

    public void register(RegisterRequestDTO request) {

        Random random = new Random();
        String code = String.format("%05d", random.nextInt(100000));


        User user = null;

        if(request.getRole().equals(Role.OWNER)){
            var owner = Owner.builder()
                    .firstName(request.getFirstName())
                    .lastname(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .emailConfirmed(false)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.OWNER)
                    .blocked(false)
                    .verification(new Verification(code, LocalDateTime.now().plusDays(1)))
                    .accommodations(new ArrayList<Accommodation>())
                    .accommodationRequests(new ArrayList<>())
                    .ownerReviews(new ArrayList<>())
                    .reviewComplaints(new ArrayList<>())
                    .build();
            user = owner;
            ownerRepository.save(owner);
        }else{
            var guest = Guest.builder()
                    .firstName(request.getFirstName())
                    .lastname(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .emailConfirmed(false)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.OWNER)
                    .verification(new Verification(code, LocalDateTime.now().plusDays(1)))
                    .blocked(false)
                    .reservations(new ArrayList<>())
                    .ownerReviews(new ArrayList<>())
                    .accommodationReviews(new ArrayList<>())
                    .favorites(new ArrayList<>())
                    .reviewComplaints(new ArrayList<>())
                    .build();
            user = guest;
            guestRepository.save(guest);
        }

        try {
            sendVerificationEmail(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //sendVerificationSms(user.getPhoneNumber());

    }

    public void sendVerificationSms(String phoneNumberString){


        PhoneNumber phoneNumber = PhoneNumber.fetcher(phoneNumberString).fetch();



        if(!phoneNumber.getValid()) throw new InvalidInputException("User with this phone number doesn't exist");


        com.twilio.rest.verify.v2.service.Verification verification = com.twilio.rest.verify.v2.service.Verification
                .creator(twilioService.getService().getSid(),
                phoneNumberString,
                "sms").create();

    }

    public void sendVerificationEmail(User user) throws MessagingException, IOException {
        Email from = new Email("mobilnebackendtest@gmail.com");
        String subject = "Verify Email Address";
        Email to = new Email(user.getEmail());


        Personalization personalization = new Personalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("firstName", user.getFirstName());
        personalization.addDynamicTemplateData("verificationLink", "http://localhost:8080/api/v1/auth/activate/" + user.getVerification().getVerificationCode());

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        mail.setTemplateId(VERIFICATION_TEMPLATE_ID);


        SendGrid sg = new SendGrid(SENDGRID_API_KEY);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw ex;
        }
    }



    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new InvalidAuthenticationException("Email or password is invalid");
        }


        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();


        if(!user.getEmailConfirmed()) throw new EmailNotConfirmedException("Email not confirmed for this user");

        if(user.getBlocked() && user instanceof Guest) throw new InvalidAuthenticationException("Your account has been blocked. Contact support for more information");

        var jwtToken = jwtService.generateToken(user, user.getId());
        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }


    public void verifyUser(String verificationCode) {
        User user = userRepository.findUserByVerification_VerificationCode(verificationCode).orElse(null);

        if (user == null) {
            throw new NonExistingVerificationCodeException("That verification code does not exist!");
        } else if (user.getVerification().getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new CodeExpiredException("Verification code expired. Register again!");
        }else if (user.getEmailConfirmed()){
            throw new UserAlreadyConfirmedException("User is already confirmed");
        }
        else {
            user.setEmailConfirmed(true);
            userRepository.save(user);
        }
    }

    public void checkVerificationSms(String smsCode, String email) {

        var userWrapper = userRepository.findByEmail(email);
        if(userWrapper.isEmpty()) throw new NonExistingEntityException("User with this email doesn't exist");
        var user = userWrapper.get();



        com.twilio.rest.verify.v2.service.VerificationCheck verificationCheck = com.twilio.rest.verify.v2.service.VerificationCheck.creator(
                        twilioService.getService().getSid())
                .setTo(user.getPhoneNumber())
                .setCode(smsCode)
                .create();

        if(!verificationCheck.getValid()) throw new InvalidAuthenticationException("The sms code is invalid");


        if(!user.getEmailConfirmed()){
            try {
                sendVerificationEmail(user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
