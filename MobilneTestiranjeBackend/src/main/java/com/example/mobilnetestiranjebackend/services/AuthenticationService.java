package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.helpers.TwilioService;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.twilio.rest.lookups.v2.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final VerificationEmailChangeRepository verificationEmailChangeRepository;
    private final VerificationRepository verificationRepository;
    private final AccommodationRepository accommodationRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;

    @Value("${spring.sendgrid.api-key}")
    private String SENDGRID_API_KEY;

    private static final String VERIFICATION_TEMPLATE_REGISTER_ID = "d-3d2f42ee76ed4904bb916951f3471b95";
    private static final String VERIFICATION_TEMPLATE_VERIFICATION_CODE_ID = "d-ae64c14258e64521b9d4978bba356b73";

    public void userExist(String email, String phoneNumber){
        userRepository.findByEmail(email).ifPresent(user -> {throw new EntityAlreadyExistsException("User with this email already exists");});
        userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {throw new EntityAlreadyExistsException("User with this phone number already exists");});
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
                    .emailChangeVerification(null)
                    .accommodations(new ArrayList<Accommodation>())
                    .accommodationRequests(new ArrayList<>())
                    .ownerReviews(new ArrayList<>())
                    .reviewComplaints(new ArrayList<>())
                    .build();
            user = ownerRepository.save(owner);

            List<NotificationPreferences> preferences = List.of(
                    NotificationPreferences.builder()
                            .userId(owner.getId())
                            .notificationType(NotificationType.RESERVATION_REQUEST)
                            .isEnabled(true)
                            .build(),
                    NotificationPreferences.builder()
                            .userId(owner.getId())
                            .notificationType(NotificationType.RESERVATION_CANCELLATION)
                            .isEnabled(true)
                            .build(),
                    NotificationPreferences.builder()
                            .userId(owner.getId())
                            .notificationType(NotificationType.OWNER_REVIEW)
                            .isEnabled(true)
                            .build(),
                    NotificationPreferences.builder()
                            .userId(owner.getId())
                            .notificationType(NotificationType.ACCOMMODATION_REVIEW)
                            .isEnabled(true)
                            .build());

            notificationPreferencesRepository.saveAll(preferences);

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
                    .emailChangeVerification(null)
                    .ownerReviews(new ArrayList<>())
                    .accommodationReviews(new ArrayList<>())
                    .favorites(new ArrayList<>())
                    .reviewComplaints(new ArrayList<>())
                    .build();
            user = guestRepository.save(guest);

            NotificationPreferences preference = NotificationPreferences.builder()
                    .userId(guest.getId())
                    .notificationType(NotificationType.RESERVATION_RESPONSE)
                    .isEnabled(true)
                    .build();
            notificationPreferencesRepository.save(preference);
        }

        try {
            sendVerificationEmail(user, true);
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

    public void sendVerificationEmail(User user, Boolean isConfirmationCodeMail) throws MessagingException, IOException {
        Email from = new Email("mobilnebackendtest@gmail.com");
        String subject = "";

        String toEmail = "";
        if(isConfirmationCodeMail){
            toEmail = user.getEmail();
        }else{
            Optional<VerificationEmailChange> verWrapper = verificationEmailChangeRepository.findByUserId(user.getId());
            var verName = verWrapper.get();
            toEmail = verName.getNewEmail();
        }
        Email to = new Email(toEmail);


        Personalization personalization = new Personalization();
        personalization.addTo(to);


        Mail mail = new Mail();
        mail.setFrom(from);
        if(isConfirmationCodeMail) {
            subject = "Verify email Address";
            personalization.addDynamicTemplateData("firstName", user.getFirstName());
            personalization.addDynamicTemplateData("verificationLink", "http://localhost:8080/api/v1/auth/activate/" + user.getVerification().getVerificationCode());
            mail.setTemplateId(VERIFICATION_TEMPLATE_REGISTER_ID);
        }else{
            subject = "Verification code for email change";
            personalization.addDynamicTemplateData("email", user.getEmail());
            personalization.addDynamicTemplateData("confirmationCode", user.getEmailChangeVerification().getVerificationCode());
            mail.setTemplateId(VERIFICATION_TEMPLATE_VERIFICATION_CODE_ID);
        }
        mail.setSubject(subject);
        mail.addPersonalization(personalization);

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


        if(user.getEmailChangeVerification() != null){
            Optional<VerificationEmailChange> verWrapper = verificationEmailChangeRepository.findByUserId(user.getId());
            var ver = verWrapper.get();

            user.setEmailChangeVerification(null);
            user = userRepository.save(user);

            verificationEmailChangeRepository.delete(ver);
        }


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
            var newEmail = user.getEmailChangeVerification().getNewEmail();
            if(user.getRole().equals(Role.OWNER)){
                List<Accommodation> accommodations = accommodationRepository.findByOwnerId(user.getId());
                for(Accommodation accommodation: accommodations){
                    String[] pathParts = accommodation.getImagePaths().get(0).split("/");
                    File oldFolder = new File("uploads/" + user.getEmail());
                    File newFolder = new File("uploads/" + newEmail);
                    if (oldFolder.renameTo(newFolder)) {
                        System.out.println("Folder renamed successfully!");
                    } else {
                        System.err.println("Failed to rename folder.");
                    }

                    for (int i = 0; i < accommodation.getImagePaths().size(); i++) {
                        String path = accommodation.getImagePaths().get(i);
                        String[] split = path.split("/");
                        accommodation.getImagePaths().set(i, "/" + newEmail + "/" + split[2] + "/" + split[3]);
                    }
                }
            }

            user.setEmail(newEmail);
            userRepository.save(user);
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
                sendVerificationEmail(user, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void validateCode(User user, String verification, String newEmail) {

        Optional<User> checkWrapper = userRepository.findByEmail(newEmail);
        if(checkWrapper.isPresent()) throw new EntityAlreadyExistsException("User with this email already exists");


        Optional<User> userWrapper = userRepository.findByEmail(user.getEmail());
        user = userWrapper.get();

        if(user.getEmailChangeVerification().getExpirationDate().isBefore(LocalDateTime.now())) throw new InvalidAuthorizationException("The code has expired, please try again");
        if(!user.getEmailChangeVerification().getVerificationCode().equals(verification)) throw new InvalidAuthenticationException("Code is incorrect");

        Random random = new Random();
        String code = String.format("%05d", random.nextInt(100000));

        Optional<Verification> verWrapper = verificationRepository.findByUserId(user.getId());
        var ver = verWrapper.get();


        user.getEmailChangeVerification().setNewEmail(newEmail);
        user.getEmailChangeVerification().setOldEmail(user.getEmail());
        user.setVerification(new Verification(code, LocalDateTime.now().plusDays(1)));
        user = userRepository.save(user);
        verificationRepository.delete(ver);

        try {
            sendVerificationEmail(user, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void sendEmailVerificationCode(User user) {

        Optional<User> userWrapper = userRepository.findByEmail(user.getEmail());
        user = userWrapper.get();

        if(user.getEmailChangeVerification() == null || user.getEmailChangeVerification().getExpirationDate().isBefore(LocalDateTime.now())){
            Random random = new Random();
            String code = String.format("%05d", random.nextInt(100000));

           //OVDE SE VRATITI ZA SLUCAJ KADA KORISNIK PONOVO MENJA EMAIL NAKON STO GA JE VEC PROMENIO I NIJE SE IZLOGOVAO

            var verification = new VerificationEmailChange();
            verification.setNewEmail(null);
            verification.setVerificationCode(code);
            verification.setExpirationDate(LocalDateTime.now().plusMinutes(5));
            verification = verificationEmailChangeRepository.save(verification);

            user.setEmailChangeVerification(verification);
            user = userRepository.save(user);


            try {
                sendVerificationEmail(user, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new EntityAlreadyExistsException("An verification code has been already sent to email " + user.getEmail());
        }

    }

}
