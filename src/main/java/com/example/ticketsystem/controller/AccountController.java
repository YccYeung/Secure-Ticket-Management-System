package com.example.ticketsystem.controller;

import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.ticketsystem.model.SetupRequest;
import com.example.ticketsystem.service.AccountService;

@RequestMapping("/api/account")
@RestController
public class AccountController {
    
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> userAccount(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        boolean setupComplete = accountService.isSetupComplete(keycloakId);
        return ResponseEntity.ok(Map.of("setupComplete", setupComplete));
    }

    @PostMapping("/setup")
    public ResponseEntity<?> userAccountSetup(@AuthenticationPrincipal Jwt jwt, @RequestBody SetupRequest request) {
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        
        if (!accountService.newCardNumberPolicy(request.getCardNumber())) {
            return ResponseEntity.badRequest().body("Invalid card number");
        }
        
        boolean success = accountService.setupAccount(username, keycloakId, request.getCardNumber());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Account setup complete"));
        }
        return ResponseEntity.badRequest().body("Setup failed");
    }

    @GetMapping("/balance")
    public ResponseEntity<?> userAccountBalance(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return ResponseEntity.ok(Map.of("balance", accountService.getBalance(keycloakId)));
    }
}
