package com.example.bacourse.repository;

import java.util.List;


import com.example.bacourse.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    <Optional> User findByEmail(String email);
    //Optional<User> findByUsernameOrEmail(String email);
    List<User> findByIdIn(List<Long> userIds);
    Boolean existsByEmail(String email);
    public User findByResetPasswordToken(String token);

    
}
