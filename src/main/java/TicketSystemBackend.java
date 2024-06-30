import org.passay.*;
import java.util.Arrays;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Backend functionality for the Ticket Management System.
 *
 * This class handles various backend operations including
 * user login and registration, and providing different functions
 * for applications
 *
 */
public class TicketSystemBackend {

    public TicketSystemDB ticketSystemDB = new TicketSystemDB();

    public TicketSystemBackend() throws SQLException {}

    /**
     * Interface for user login.
     *
     * This method handles the user login process by prompting the user to enter their username and password.
     * It verifies the username and password against the backend database and grants access if the credentials are valid.
     *
     * The user is prompted to re-enter their credentials if the provided username or password is invalid.
     * Once the user is successfully logged in, they are redirected to their user dashboard.
     *
     * @throws SQLException If a database access error occurs.
     */
    public static void loginInterface() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        TicketSystemBackend backend = new TicketSystemBackend();

        System.out.println("\n");
        System.out.println("===================================================");
        System.out.println("|                                                 |");
        System.out.println("|        \u001B[1m\u001B[34mLOGIN TO TICKET MANAGEMENT SYSTEM\u001B[0m        |");
        System.out.println("|                                                 |");
        System.out.println("===================================================");

        System.out.print("|  \u001B[33mPlease Enter your username:\u001B[0m ");
        String usernameCheck = scanner.next(); // receive username from user input

        while (!backend.userNameVerification(usernameCheck)) {
            System.out.println("|  \u001B[31mThe username is invalid! Please try again\u001B[0m");
            System.out.print("|  \u001B[33mPlease Enter your username:\u001B[0m ");
            usernameCheck = scanner.next();
        }

        System.out.print("|  \u001B[33mPlease Enter your password:\u001B[0m ");
        String passwordCheck = scanner.next(); // receive password from user input
        while (!backend.passwordVerification(usernameCheck, passwordCheck)) {
            System.out.println("|  \u001B[31mThe password is invalid! Please try again\u001B[0m");
            System.out.print("|  \u001B[33mPlease Enter your password:\u001B[0m ");
            passwordCheck = scanner.next();
        }

        System.out.println("|  \u001B[32mUser: " + usernameCheck + " login successfully!\u001B[0m");
        System.out.println("===================================================");
        TicketSiteUser ticketSiteUser = new TicketSiteUser(usernameCheck, passwordCheck);
        ticketSiteUser.userDashBoard();
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
     * Interface for Register new user
     *
     * This method prompts the user to enter their username, password, and credit card number.
     * It enforces policies on username, password, and credit card number before registering the user.
     *
     * @throws SQLException if a database access error occurs
     */
    public static void registerInterface() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        TicketSystemBackend backend = new TicketSystemBackend();

        System.out.println("\n");
        System.out.println("===================================================");
        System.out.println("|                                                 |");
        System.out.println("|        \u001B[1m\u001B[34mREGISTER FOR NEW USER\u001B[0m                    |");
        System.out.println("|                                                 |");
        System.out.println("===================================================");

        System.out.print("|  \u001B[33mPlease Enter your username:\u001B[0m ");
        String newUserName = scanner.next();
        while (!backend.newUsernamePolicy(newUserName)) {
            System.out.println("|  \u001B[31mUsername length must be between 6-20 characters and must not duplicate existing users.\u001B[0m");
            System.out.print("|  \u001B[33mPlease Enter your username:\u001B[0m ");
            newUserName = scanner.next();
        }

        System.out.print("|  \u001B[33mPlease Enter your password:\u001B[0m ");
        String newPassword = scanner.next();
        while (!backend.newPasswordPolicy(newPassword)) {
            System.out.println("|  \u001B[31mPassword length must be between 8-20 characters long and include at least 3 different character categories.\u001B[0m");
            System.out.print("|  \u001B[33mPlease Enter your password:\u001B[0m ");
            newPassword = scanner.next();
        }

        System.out.print("|  \u001B[33mPlease Enter your credit card number:\u001B[0m ");
        String newCardNumber = scanner.next();
        while (!backend.newCardNumberPolicy(newCardNumber)) {
            System.out.println("|  \u001B[31mCredit Card Number must be a 16-digit number.\u001B[0m");
            System.out.print("|  \u001B[33mPlease Enter your credit card number:\u001B[0m ");
            newCardNumber = scanner.next();
        }
        backend.userRegister(newUserName, newPassword, newCardNumber);
        System.out.println("|  \u001B[32mUser: " + newUserName + " register successfully!\u001B[0m");
    }

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
        if (newUsername == null || newUsername.length() <= 6 || newUsername.length() >= 20) {return false;}
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


