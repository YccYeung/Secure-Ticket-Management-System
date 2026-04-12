package com.example.ticketsystem.model;

public class SetupRequest {

    private String cardNumber;

    public SetupRequest(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
}
