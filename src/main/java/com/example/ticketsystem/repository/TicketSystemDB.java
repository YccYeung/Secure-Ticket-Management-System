package com.example.ticketsystem.repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.service.AESEncryption;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import javax.sql.DataSource;

import java.sql.*;
import java.util.HashMap;

/**
 * Provides database access and operations for the Ticket Management System.
 * This class handles connections to the database, executing queries, and managing
 * game and ticket data.
 *
 */
@Repository
public class TicketSystemDB {

  private final DataSource dataSource;

  @Value("${aes_key}")
  private String secretKey;
  
  public HashMap<String, Integer> gameNameAllowList = new HashMap<>();
  public HashMap<String, Integer> availableToSellList = new HashMap<>();

  public TicketSystemDB(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @PostConstruct
  public void init() {
      createTable();
  }

  public void createTable() {
    Connection connection = null;
    Statement statement = null;
    // Create users table
    try {
      connection = dataSource.getConnection();;
      statement = connection.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS users (" +
          "id INT AUTO_INCREMENT PRIMARY KEY, " +
          "username VARCHAR(50) NOT NULL, " +
          "keycloak_id VARCHAR(100) NOT NULL, " +
          "cardNumber VARCHAR(100) NOT NULL, " +
          "money int NOT NULL)";
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    // Create tickets table
    try {
      connection = dataSource.getConnection();;
      statement = connection.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS tickets (" +
          "id INT AUTO_INCREMENT PRIMARY KEY, " +
          "name VARCHAR(50) NOT NULL, " +
          "location VARCHAR(50) NOT NULL, " +
          "price DOUBLE NOT NULL, " +
          "quantity INT NOT NULL, " + 
          "event_date DATE NOT NULL)";
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage()); 
    }

    // Create user_tickets table
    try {
      connection = dataSource.getConnection();;
      statement = connection.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS user_tickets (" +
          "id INT AUTO_INCREMENT PRIMARY KEY, " +
          "quantity INT NOT NULL, " +
          "user_id INT NOT NULL, " +
          "ticket_id INT NOT NULL, " +
          "UNIQUE KEY unique_user_ticket (user_id, ticket_id), " + 
          "FOREIGN KEY (user_id) REFERENCES users(id), " +
          "FOREIGN KEY (ticket_id) REFERENCES tickets(id))";
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage()); 
    } 
  }

  public void createUser(String username, String keycloak_id, String cardNumber) throws SQLException{
    String sql = "INSERT INTO users (username, keycloak_id, cardNumber, money) VALUES (?,?,?,?) ";
    String encryptedCardNumber = "";
    try {
      SecretKey key = AESEncryption.decodeKey(secretKey);
      encryptedCardNumber = AESEncryption.encrypt(cardNumber, key);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);

      preparedStatement.setString(1, username);
      preparedStatement.setString(2, keycloak_id);
      preparedStatement.setString(3, encryptedCardNumber);
      preparedStatement.setInt(4, 0);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public boolean keycloakIdVerify(String keycloak_id) {
    String sql = "SELECT * from users where keycloak_id = ?";

    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, keycloak_id);

      try (ResultSet resultSet = preparedStatement.executeQuery()){
        if (resultSet.next()) {
          return true;
        }
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return false;
  }

  /**
   * Verifies the provided credit card number for the specified username against the stored encrypted credit card number in the database.
   *
   * This method connects to the database, retrieves the stored encrypted credit card number for the given username,
   * decrypts it using AES encryption, and compares it with the provided credit card number.
   *
   * The method returns true if the provided credit card number matches the decrypted stored credit card number, otherwise false.
   *
   *
   * @param username The username whose credit card number is to be verified.
   * @param cardNumber The credit card number to be verified.
   * @return true if the provided credit card number matches the decrypted stored credit card number, false otherwise.
   * @throws SQLException If a database access error occurs.
   */
  public boolean creditCardVerify(String username, String cardNumber) throws SQLException {
    String sql = "select cardNumber from users where username = ?";
    try  {
      Connection connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, username);
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        String encryptedCardNumber = resultSet.getString("cardNumber");
        try {
          SecretKey key = AESEncryption.decodeKey(secretKey);
          String decryptedCardNumber = AESEncryption.decrypt(encryptedCardNumber, key);
          return decryptedCardNumber.equals(cardNumber);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return false;
  }

  /**
   * Retrieves and decrypts the stored credit card number for the specified username.
   *
   * This method connects to the database, retrieves the encrypted credit card number for the given username,
   * decrypts it using AES encryption, and returns the decrypted (plain) credit card number.
   *
   * The method returns an empty string if the username is not found or if any error occurs during the process.
   *
   * @param username The username whose credit card number is to be retrieved and decrypted.
   * @return The decrypted (plain) credit card number, or an empty string if an error occurs.
   * @throws SQLException If a database access error occurs.
   */
  public String getCardNumber(String username) throws SQLException {
    String plainCardNumber = "";
    String sql = "select cardNumber from users where username = ?";
    try  {
      Connection connection = dataSource.getConnection();;
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, username);
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        String encryptedCardNumber = resultSet.getString("cardNumber");
        try {
          SecretKey key = AESEncryption.decodeKey(secretKey);
          plainCardNumber = AESEncryption.decrypt(encryptedCardNumber, key);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return plainCardNumber;
  }

  /**
   * Deposits a specified amount of money into the user's account.
   *
   * This method connects to the database and updates the user's account balance by adding the specified
   * deposit amount to the existing balance.
   *
   * The method uses a parameterized SQL query to prevent SQL injection attacks and ensure safe execution.
   *
   * @param username The username of the user who is depositing the money.
   * @param moneyAmount The amount of money to be deposited.
   * @throws SQLException If a database access error occurs.
   */
  public void depositMoney(String username, Double moneyAmount) throws SQLException {
    String sql = "Update users set money = money + ? where username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, moneyAmount);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Checks if the user's account balance is sufficient for a specified amount.
   * This method connects to the database, retrieves the user's account balance,
   * and compares it to the specified amount.
   *
   * @param username The username of the user whose account balance is to be checked.
   * @param moneyAmount The amount to check against the user's account balance.
   * @return true if the account balance is sufficient, false otherwise.
   * @throws SQLException If a database access error occurs.
   */
  public boolean checkAccountBalance(String username, Double moneyAmount) throws SQLException {
    String sql = "Select money from users where username = ?";

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setString(1, username);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          if (resultSet.next()) {
            double accountBalance = resultSet.getDouble("money");
            return accountBalance >= moneyAmount;
          }

        } catch (Exception e) {
          System.out.println(e.getMessage());
        }

      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }

    return false;
  }

  /**
   * Verifies if there are enough tickets available for a specified game.
   * This method connects to the database, retrieves the available quantity of tickets for the specified game,
   * and compares it to the requested number of tickets.
   *
   * @param gameName The name of the game for which ticket availability is to be checked.
   * @param ticketNumber The number of tickets to check for availability.
   * @return true if the available quantity is sufficient, false otherwise.
   * @throws SQLException If a database access error occurs.
   */
  public boolean ticketQuantityVerify(String gameName, int ticketNumber) throws SQLException{
    String sql = "Select quantity from tickets where name = ?";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, gameName);
      try (ResultSet resultSet = preparedStatement.executeQuery()){
        if (resultSet.next()) {
          double quantity = resultSet.getDouble("quantity");
          return quantity >= ticketNumber;
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  /**
   * Calculates the total cost of tickets for a specified game.
   * This method connects to the database, retrieves the price of a single ticket for the specified game,
   * and multiplies it by the number of tickets to calculate the total cost.
   *
   * @param gameName The name of the game for which the total ticket cost is to be calculated.
   * @param ticketNumber The number of tickets.
   * @return The total cost of the specified number of tickets.
   */
  public double ticketTotalCost(String gameName, int ticketNumber) {
    String sql = "Select price from tickets where name = ?";
    double totalCost = 0.0;

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setString(1, gameName);

        try (ResultSet resultSet = preparedStatement.executeQuery()){
          if (resultSet.next()) {
            double cost = resultSet.getDouble("price");
            totalCost = cost * ticketNumber;
          }
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return totalCost;
  }

  /**
   * Deducts a specified amount of money from the user's account for a purchase.
   * This method connects to the database and updates the user's account balance by subtracting the specified amount.
   *
   * @param username The username of the user making the purchase.
   * @param moneyAmount The amount of money to be deducted from the user's account.
   * @throws SQLException If a database access error occurs.
   */
  public void purchaseRequest(String username, Double moneyAmount) throws SQLException {
    String sql = "Update users set money = money - ? where username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, moneyAmount);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Adds a specified amount of money to the user's account as a refund.
   * This method connects to the database and updates the user's account balance by adding the specified amount.
   *
   * @param username The username of the user receiving the refund.
   * @param moneyAmount The amount of money to be added to the user's account.
   * @throws SQLException If a database access error occurs.
   */
  public void refundRequest(String username, Double moneyAmount) throws SQLException {
    String sql = "Update users set money = money + ? where username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, moneyAmount);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Updates the quantity of tickets for a specified game.
   * This method connects to the database and updates the quantity of tickets available for the specified game.
   *
   * @param gameName The name of the game for which the ticket quantity is to be updated.
   * @param ticketNumber The number of tickets to be added (positive) or removed (negative) from the current quantity.
   * @throws SQLException If a database access error occurs.
   */
  public void updateTicketQuantity(String gameName, int ticketNumber) throws SQLException {
    String sql = "Update tickets set quantity = quantity + ? where name = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, ticketNumber);
      preparedStatement.setString(2, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Retrieves the quantity of tickets a user holds for a specified game.
   * This method connects to the database, retrieves the quantity of tickets held by the user for the specified game,
   * and returns the quantity.
   *
   * @param username The username of the user.
   * @param gameName The name of the game for which the ticket quantity is to be retrieved.
   * @return The quantity of tickets the user holds for the specified game.
   * @throws SQLException If a database access error occurs.
   */
  public int getUserTicketQuantity(String username, String gameName) throws SQLException {
    String sql = "Select quantity from user_tickets " +
        "where user_id = (SELECT id from users where username = ?) " +
        "AND ticket_id = (SELECT id from tickets where name = ?)";
    int ticketNumber = 0;

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, gameName);

      try (ResultSet resultSet = preparedStatement.executeQuery()){
        if (resultSet.next()) {
          ticketNumber = resultSet.getInt("quantity");
          return ticketNumber;
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return ticketNumber;
  }

  /**
   * Creates or updates a record of tickets purchased by a user for a specified game.
   * This method inserts a new record into the user_tickets table or updates an existing record if it already exists.
   *
   * @param username The username of the user purchasing the tickets.
   * @param gameName The name of the game for which the tickets are purchased.
   * @param ticketNumbers The number of tickets purchased.
   */
  public void createUserTicketsRecord(String username, String gameName, int ticketNumbers) {
    String sql = "INSERT INTO user_tickets (user_id, ticket_id, quantity) " +
        "SELECT u.id, t.id, ? from users u, tickets t where u.username = ? and t.name = ?" +
        "ON DUPLICATE KEY UPDATE user_tickets.quantity = user_tickets.quantity + VALUES(quantity)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, ticketNumbers);
      preparedStatement.setString(2, username);
      preparedStatement.setString(3, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Updates the number of tickets held by a user for a specified game.
   * This method connects to the database and decreases the quantity of tickets held by the user for the specified game.
   *
   * @param username The username of the user whose ticket quantity is to be updated.
   * @param gameName The name of the game for which the ticket quantity is to be updated.
   * @param ticketNumbers The number of tickets to be subtracted from the user's current holding.
   */
  public void updateUserTicketsNumber(String username, String gameName, int ticketNumbers) {
    String sql = "Update user_tickets set quantity = quantity - ? " +
        "where user_id = (SELECT id from users where username = ?)" +
        "AND ticket_id = (SELECT id from tickets where name = ?)";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, ticketNumbers);
      preparedStatement.setString(2, username);
      preparedStatement.setString(3, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Deletes a user's ticket record for a specified game.
   * This method connects to the database and deletes the user's ticket record for the specified game.
   *
   * @param username The username of the user whose ticket record is to be deleted.
   * @param gameName The name of the game for which the ticket record is to be deleted.
   */
  public void deleteUserTicketsRecord(String username, String gameName) {
    String sql = "DELETE from user_tickets where user_id = (SELECT id from users where username = ?) " +
        "AND ticket_id = (SELECT id from tickets where name = ?)";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}