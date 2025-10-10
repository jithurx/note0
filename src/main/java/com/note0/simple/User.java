package com.note0.simple;

/**
 * A Plain Old Java Object (POJO) that represents a User in our application.
 * Its only job is to hold user data.
 */
public class User {

    // Populated by the database after a user is retrieved (e.g., during login)
    private long id;
    private String fullName;
    private String email;
    private String role;

    // This field is only used temporarily to hold the plain-text password
    // during the registration process before it gets hashed.
    private String password;

    // This field is used to hold the secure, hashed password
    // when we retrieve it from the database for login verification.
    private String passwordHash;


    // --- Getters and Setters ---
    // These public methods allow other parts of our code to safely access
    // and modify the private fields of this class.

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}