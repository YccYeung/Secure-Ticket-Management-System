package com.example.ticketsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketsystem.model.LoginRequest;
import com.example.ticketsystem.model.RegisterRequest;
import com.example.ticketsystem.service.AuthService;
import com.example.ticketsystem.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (authService.register(request.getUsername(), request.getPassword(), request.getCardNumber())) {
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.badRequest().body("Registration failed");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (authService.login(request.getUsername(),request.getPassword())) {
            return ResponseEntity.ok(jwtService.generateToken(request.getUsername()));
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
}