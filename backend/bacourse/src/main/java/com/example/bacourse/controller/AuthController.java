package com.example.bacourse.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.CredentialNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.example.bacourse.exception.AppException;
import com.example.bacourse.model.Role;
import com.example.bacourse.model.RoleName;
import com.example.bacourse.model.User;
import com.example.bacourse.payloads.ApiResponse;
import com.example.bacourse.payloads.ForgotPasswordRequest;
import com.example.bacourse.payloads.JwtAuthenticationResponse;
import com.example.bacourse.payloads.LoginRequest;
import com.example.bacourse.payloads.ResetPasswordRequest;
import com.example.bacourse.payloads.SignUpRequest;
import com.example.bacourse.repository.RoleRepository;
import com.example.bacourse.repository.UserRepository;
import com.example.bacourse.security.CustomUserDetailsService;
import com.example.bacourse.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import net.bytebuddy.utility.RandomString;

@CrossOrigin(origins="*", allowedHeaders="*", exposedHeaders="*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CustomUserDetailsService customerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getEmail(),signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getEmail()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, jwt));
    }


   

// Forgot password
@PostMapping("/forgot-password")
public ResponseEntity<?> processForgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();
        String token = RandomString.make(30);
         
        try {
            customerService.updateResetPasswordToken(token, email);
            String resetPasswordLink = "http://localhost:3000/reset-password/" + token;
            sendEmail(email, resetPasswordLink);
           
             
        } catch (UsernameNotFoundException ex) {
                return new ResponseEntity(new ApiResponse(false, "Cannot find user with this email address."), HttpStatus.BAD_REQUEST); 
            } catch (UnsupportedEncodingException | MessagingException e) {
                return new ResponseEntity(new ApiResponse(false, "Error while sending email."), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        return ResponseEntity.ok(new ApiResponse(true, "An email with reset instructions has been sent to your email address."));
        }

        public void sendEmail(String recipientEmail, String link)
        throws MessagingException, UnsupportedEncodingException {
                MimeMessage message = mailSender.createMimeMessage();              
                MimeMessageHelper helper = new MimeMessageHelper(message);
     
        helper.setFrom("andra.mertilos@intys.eu", "Simple Business Analysis Course Support");
        helper.setTo(recipientEmail);
     
        String subject = "Simple Business Analysis: Here's the link to reset your password";
     
        String content = "<p>Hello,</p>"
            + "<p>You have requested to reset your password for the Simple Business Analysis online course.</p>"
            + "<p>Click the link below to change your password:</p>"
            + "<p><a href=\"" + link + "\">Change my password</a></p>"
            + "<br>"
            + "<p>Ignore this email if you do remember your password, "
            + "or you have not made the request.</p>";
     
        helper.setSubject(subject);
     
        helper.setText(content, true);
     
        mailSender.send(message);
}

@PostMapping("/reset-password")
public String processResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    String token = request.getToken();
    String password = request.getPassword();
     
    User user = customerService.getByResetPasswordToken(token);
   
     
    if (user == null) {

        return "Invalid Token";
    } else {           
        customerService.updatePassword(user, password);
         
      
    }
     
    return "You have successfully changed your password.";
}


    }




