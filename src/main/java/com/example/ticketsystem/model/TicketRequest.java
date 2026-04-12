package com.example.ticketsystem.model;

public class TicketRequest {
    
    private String ticketName;
    private int quantity;

    public TicketRequest(String ticketName, int quantity) {
        this.ticketName = ticketName;
        this.quantity = quantity;
    }

    public String getTicketName() {
        return this.ticketName;
    }

    public int getQuantity() {
        return this.quantity;
    }
}