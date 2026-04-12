package com.example.ticketsystem.service;

import java.sql.SQLException;
import org.springframework.stereotype.Service;

import com.example.ticketsystem.repository.TicketSystemDB;

@Service
public class AccountService {
    private final TicketSystemDB ticketSystemDB;

    public AccountService(TicketSystemDB ticketSystemDB) {
        this.ticketSystemDB = ticketSystemDB;
    }

    public boolean isSetupComplete(String keycloakId) {
        return ticketSystemDB.keycloakIdVerify(keycloakId);
    }

    public boolean setupAccount(String username, String keycloakId, String cardNumber) {
        try {
            ticketSystemDB.createUser(username, keycloakId, cardNumber);
            return true;
        } catch (SQLException e) {
            return false;
        }   
    }

    public boolean newCardNumberPolicy(String newCardNumber) {
        return newCardNumber.length() == 16 && newCardNumber.matches("\\d+");
    }

    public int getBalance(String keycloakId) {
        try {
            return ticketSystemDB.getBalance(keycloakId);
        } catch (SQLException e) {
            return -1;
        }
    }
}