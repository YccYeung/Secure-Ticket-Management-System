package com.example.ticketsystem.service;
import org.passay.*;
import org.springframework.stereotype.Service;

import com.example.ticketsystem.repository.TicketSystemDB;

import java.util.Arrays;
import java.sql.SQLException;

/**
 * Backend functionality for the Ticket Management System.
 *
 * This class handles various backend operations including
 * user login and registration, and providing different functions
 * for applications
 *
 */
@Service
public class TicketSystemBackend {

    private final TicketSystemDB ticketSystemDB;

    public TicketSystemBackend(TicketSystemDB ticketSystemDB) {
        this.ticketSystemDB = ticketSystemDB;
    }

    /**
     * Verifies the given username against the database.
     * <p>
     * This method checks if the provided username exists in the database.
     *
     * @param username The username to be verified.
     * @return true if the username exists in the database, false otherwise.
     */
    public boolean userNameVerification(String username) {return ticketSystemDB.userVerify(username);}

    /**
     * Verifies the given password for the specified username against the database.
     *
     * This method checks if the provided password matches the stored password for the given username.
     *
     * @param username The username whose password is to be verified.
     * @param password The password to be verified.
     * @return true if the password matches the stored password for the username, false otherwise.
     */
    public boolean passwordVerification(String username, String password) {return ticketSystemDB.passwordVerify(username, password);}

    /**
     * Registers a new user with the provided username, password, and card number.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @param cardNumber the credit card number of the new user
     * @throws SQLException if a database access error occurs
     */
    public void userRegister(String username, String password, String cardNumber)
        throws SQLException {ticketSystemDB.createUser(username, password, cardNumber);}

    /**
     * Validates the new username according to the policy.
     *
     * The username must be between 6 and 20 characters long and must not duplicate an existing username.
     *
     * @param newUsername the username to be validated
     * @return true if the username meets the policy, false otherwise
     */
    public boolean newUsernamePolicy(String newUsername) {
        if (newUsername == null || newUsername.length() < 6 || newUsername.length() > 20) {return false;}
        return !ticketSystemDB.userVerify(newUsername);
    }

    /**
     * Validates the new password according to the policy.
     *
     * The password must be between 8 and 20 characters long, containing at least one upper case letter,
     * one lower case letter, one digit, and one special character, and must not contain any whitespace.
     *
     * @param newPassword the password to be validated
     * @return true if the password meets the policy, false otherwise
     */
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

    /**
     * Validates the new credit card number according to the policy.
     *
     * The credit card number must be a 16-digit number.
     *
     * @param newCardNumber the credit card number to be validated
     * @return true if the credit card number meets the policy, false otherwise
     */
    public boolean newCardNumberPolicy(String newCardNumber) {
        return newCardNumber.length() == 16 && newCardNumber.matches("\\d+");
    }
}