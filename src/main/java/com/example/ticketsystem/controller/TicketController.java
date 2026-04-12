package com.example.ticketsystem.controller;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketsystem.model.TicketRequest;
import com.example.ticketsystem.service.TicketService;

@RequestMapping
@RestController
public class TicketController {

    private TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    @PreAuthorize("hasRole('user')")
    @GetMapping("/api/tickets")
    public ResponseEntity<?> getAllTicket() throws SQLException {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/api/admin/tickets")
    public ResponseEntity<?> manageTicket() {
        return ResponseEntity.ok("manage ticket");
    }

    @PreAuthorize("hasRole('user')")
    @PostMapping("/api/tickets/buy")
    public ResponseEntity<?> buyTicket(@AuthenticationPrincipal Jwt jwt, @RequestBody TicketRequest request) 
    throws SQLException {
        String keycloakId = jwt.getSubject();
        String ticketName = request.getTicketName();
        int quantity = request.getQuantity();

        return ResponseEntity.ok(ticketService.buyTicket(keycloakId, ticketName, quantity));
    }

    @PreAuthorize("hasRole('user')")
    @PostMapping("/api/tickets/sell")
    public ResponseEntity<?> sellTicket(@AuthenticationPrincipal Jwt jwt, @RequestBody TicketRequest request) 
    throws SQLException {
        String keycloakId = jwt.getSubject();
        String ticketName = request.getTicketName();
        int quantity = request.getQuantity();

        return ResponseEntity.ok(ticketService.sellTicket(keycloakId, ticketName, quantity));
    }
}
