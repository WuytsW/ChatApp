package com.example.chatapp.repository;

import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Example: find a user by username
    Optional<User> findByUsername(String username);

    // Example: check if a user exists by email
    boolean existsByEmail(String email);
}
