package com.example.ticketsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController {
    
    @PreAuthorize("hasRole('user')")
    @GetMapping("/api/tickets")
    public ResponseEntity<?> buyTicket() {
        return ResponseEntity.ok("buy ticket");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/api/admin/tickets")
    public ResponseEntity<?> manageTicket() {
        return ResponseEntity.ok("manage ticket");
    }
}
