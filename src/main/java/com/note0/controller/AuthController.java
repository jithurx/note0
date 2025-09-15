package com.note0.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.note0.dto.RegisterDto;
import com.note0.entity.User;
import com.note0.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        try {
            User newUser = new User();
            newUser.setFullName(registerDto.getFullName());
            newUser.setEmail(registerDto.getEmail());
            // We set the raw password here, the service will encode it.
            newUser.setPasswordHash(registerDto.getPassword());

            User registeredUser = authService.registerUser(newUser);
            // Don't return the password hash in the response
            registeredUser.setPasswordHash(null); 
            
            return ResponseEntity.ok(registeredUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}