package com.example.ticketsystem.service;

import java.sql.SQLException;
import java.util.Arrays;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.stereotype.Service;

import com.example.ticketsystem.repository.TicketSystemDB;

@Service
public class AuthService {
    private final TicketSystemDB ticketSystemDB;

    public AuthService(TicketSystemDB ticketSystemDB) {
        this.ticketSystemDB = ticketSystemDB;
    }

    public boolean register(String username, String password, String cardNumber) {
        if (!newUsernamePolicy(username)) {
            return false;
        } 
        if (!newPasswordPolicy(password)) {
            return false;
        } 
        if (!newCardNumberPolicy(cardNumber)) {
            return false;
        }

        try {
            userRegister(username, password, cardNumber);
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
            return false;
        }  
        return true;
    }

    public boolean login(String username, String password) {
        if (!userNameVerification(username)) {
            return false;
        }
        if (!passwordVerification(username, password)) {
            return false;
        }
        return true;
    }

    public boolean userNameVerification(String username) {
        return ticketSystemDB.userVerify(username);
    }

    public boolean passwordVerification(String username, String password) {
        return ticketSystemDB.passwordVerify(username, password);
    }

    public void userRegister(String username, String password, String cardNumber)
        throws SQLException {
            ticketSystemDB.createUser(username, password, cardNumber);
        }

    public boolean newUsernamePolicy(String newUsername) {
        if (newUsername == null || newUsername.length() < 6 || newUsername.length() > 20) {
            return false;
        }
        return !ticketSystemDB.userVerify(newUsername);
    }

    public boolean newPasswordPolicy(String newPassword) {
        if (newPassword == null) {return false;}
        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 20),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new WhitespaceRule()
        ));

        RuleResult result = passwordValidator.validate(new PasswordData(newPassword));
        return result.isValid();
    }

    public boolean newCardNumberPolicy(String newCardNumber) {
        return newCardNumber.length() == 16 && newCardNumber.matches("\\d+");
    }
}