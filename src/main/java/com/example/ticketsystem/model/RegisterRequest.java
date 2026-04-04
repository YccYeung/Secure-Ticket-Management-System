package com.example.ticketsystem.model;
public class RegisterRequest {
    private String username;
    private String password;
    private String cardNumber;

    public RegisterRequest(String username, String password, String cardNumber) {
        this.username = username;
        this.password = password;
        this.cardNumber = cardNumber;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
}

