package com.note0.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.note0.entity.User;

// This interface extends JpaRepository, giving us a ton of pre-built database methods.
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA is smart. By naming the method this way, it will
    // automatically generate the query to find a user by their email address.
    Optional<User> findByEmail(String email);
}