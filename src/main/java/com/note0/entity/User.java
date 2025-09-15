package com.note0.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String collegeName;
    private String branch;
    private String semester;

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private boolean isActive = true;

    // ★★★ ADD THIS FINAL FIELD ★★★
    // A new user should not be verified by default, so we set it to 'false'.
    @Column(nullable = false)
    private boolean isVerified = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}