package com.example.ticketsystem.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.ticketsystem.repository.TicketSystemDB;

@Service
public class TicketService {

    private final TicketSystemDB ticketSystemDB;

    public TicketService(TicketSystemDB ticketSystemDB) {
        this.ticketSystemDB = ticketSystemDB;
    }

    public List<Map<String, Object>> getAllTickets() throws SQLException {
        return ticketSystemDB.getAllTickets();
    }

    public List<Map<String, Object>> getUserTickets(String keycloakId) throws SQLException {
        return ticketSystemDB.getUserTicket(keycloakId);
    }

    public boolean buyTicket(String keycloakId, String ticketName, int quantity) throws SQLException {
        double totalCost = ticketSystemDB.ticketTotalCost(ticketName, quantity);
        // Check insufficient balance
        if (!ticketSystemDB.checkAccountBalance(keycloakId, totalCost)) {
            return false; 
        }
        // Check if not enough tickets
        if (!ticketSystemDB.ticketQuantityVerify(ticketName, quantity)) {
            return false; 
        }
        
        ticketSystemDB.purchaseRequest(keycloakId, totalCost);
        ticketSystemDB.updateTicketQuantity(ticketName, -quantity);
        ticketSystemDB.createUserTicketsRecord(keycloakId, ticketName, quantity);
        
        return true;
    }

    public boolean sellTicket(String keycloakId, String ticketName, int quantity) throws SQLException {
        int userHolding = ticketSystemDB.getUserTicketQuantity(keycloakId, ticketName);

        if (userHolding < quantity) {
            return false;
        }

        double totalRefund = ticketSystemDB.ticketTotalCost(ticketName, quantity);
        
        ticketSystemDB.refundRequest(keycloakId, totalRefund);
        ticketSystemDB.updateTicketQuantity(ticketName, quantity);
        ticketSystemDB.updateUserTicketsNumber(keycloakId, ticketName, quantity);

        if (userHolding == quantity) {
            ticketSystemDB.deleteUserTicketsRecord(keycloakId, ticketName);
        }
        
        return true;
    }
}
