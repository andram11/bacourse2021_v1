package com.example.bacourse.security;


import javax.transaction.Transactional;

import com.example.bacourse.model.User;
import com.example.bacourse.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService   {

    @Autowired
UserRepository userRepository;

public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email); 
    if(userRepository.existsByEmail(email))  {
        user.setResetPasswordToken(token);
        userRepository.save(user);
    } else {
        throw new UsernameNotFoundException("Could not find any customer with the email " + email);
    }
}

public User getByResetPasswordToken(String token) {
    return userRepository.findByResetPasswordToken(token);
}
 
public void updatePassword(User user, String newPassword) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodedPassword);
     
    user.setResetPasswordToken(null);
    userRepository.save(user);
}

@Override
@Transactional
public UserDetails loadUserByUsername(String usernameOrEmail)
        throws UsernameNotFoundException {
    // Let people login with either username or email
 User user = userRepository.findByEmail(usernameOrEmail);
 //.orElseThrow(() ->  new UsernameNotFoundException("User not found with email : " + usernameOrEmail));
    

    return UserPrincipal.create(user);
}

// This method is used by JWTAuthenticationFilter
@Transactional
public UserDetails loadUserById(Long id) {
    User user = userRepository.findById(id).orElseThrow(
        () -> new UsernameNotFoundException("User not found with id : " + id)
    );

    return UserPrincipal.create(user);
} 
    
}
