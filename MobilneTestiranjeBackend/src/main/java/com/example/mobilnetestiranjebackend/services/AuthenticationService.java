package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.model.Verification;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${spring.sendgrid.api-key}")
    private String SENDGRID_API_KEY;
    private static final String VERIFICATION_TEMPLATE_ID = "d-3d2f42ee76ed4904bb916951f3471b95";
    public Boolean userExist(String email){
        var userWrapper = userRepository.findByEmail(email);
        return userWrapper.isPresent();
    }


    public void register(RegisterRequestDTO request) {

        Random random = new Random();
        String code = String.format("%05d", random.nextInt(100000));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .emailConfirmed(false)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .verification(new Verification(code, LocalDateTime.now().plusDays(1)))
                .build();

        try {
            sendVerificationEmail(user);
        } catch (IOException e) {
            System.out.println("ERROR WITH SENDING THE MAIL");
        }

        userRepository.save(user);
    }

    public void checkInput(RegisterRequestDTO request){
        if(!request.getPassword().equals(request.getRepeatPassword()))
            throw new InvalidRepeatPasswordException("Passwords do not match");

        if(!request.getRole().equals(Role.GUEST.toString()) && !request.getRole().equals(Role.OWNER.toString()))
            throw new InvalidRoleException("Invalid user role selected");

        if(userExist(request.getEmail()))
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
    }

    private void sendVerificationEmail(User user) throws MessagingException, IOException {
        Email from = new Email("mobilnebackendtest@gmail.com");
        String subject = "Verify the registration";
        Email to = new Email(user.getEmail());


//        Content content = new Content("text/plain", "Dear " + user.getFirstName() + ","
//                + "Please click the link below to verify your registration: \n"
//                + "http://localhost:8080/api/v1/auth/activate/" + user.getVerification().getVerificationCode() + "\n"
//                + "Thank you,\n");
//        Mail mail = new Mail(from, subject, to, content);

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
            throw new InvalidAuthenticationException("Invalid credentials");
        }


        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();


        if(!user.getEmailConfirmed()) throw new EmailNotConfirmedException("Email not confirmed for this user");
        var jwtToken = jwtService.generateToken(user);
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
}
