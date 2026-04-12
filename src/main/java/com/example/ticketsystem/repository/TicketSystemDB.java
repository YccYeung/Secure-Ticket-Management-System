package com.example.ticketsystem.repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.service.AESEncryption;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import javax.sql.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


  public void depositMoney(String keycloakId, Double moneyAmount) throws SQLException {
    String sql = "Update users set money = money + ? where keycloak_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, moneyAmount);
      preparedStatement.setString(2, keycloakId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public int getBalance(String keycloakId) throws SQLException {
    String sql = "Select money from users where keycloak_id = ?"; 

    Connection connection = dataSource.getConnection();
    PreparedStatement preparedStatement = connection.prepareStatement(sql);

    preparedStatement.setString(1, keycloakId);
    try (ResultSet resultSet = preparedStatement.executeQuery()) {
      if (resultSet.next()) {
        return resultSet.getInt("money");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage()); 
    }
    
    return -1;
  }

  public boolean checkAccountBalance(String keycloakId, Double moneyAmount) throws SQLException {
    String sql = "Select money from users where keycloak_id = ?";

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setString(1, keycloakId);

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

  public List<Map<String, Object>> getAllTickets() throws SQLException {

    ArrayList<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
    String sql = "Select * from tickets";

    Connection connection = dataSource.getConnection();
    PreparedStatement preparedStatement = connection.prepareStatement(sql);

    try (ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        String id = resultSet.getString("id");
        String name = resultSet.getString("name");
        String location = resultSet.getString("location");
        double price = resultSet.getDouble("price");
        int quantity = resultSet.getInt("quantity");
        Date eventDate = resultSet.getDate("event_date");

        Map<String, Object> event = new HashMap<String, Object>(); 
        event.put("id", id);
        event.put("name", name);
        event.put("location", location);
        event.put("price", price);
        event.put("quantity", quantity);
        event.put("event_date", eventDate.toString());
        eventList.add(event); 
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return eventList;
  }

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

  public void purchaseRequest(String keycloakId, Double moneyAmount) throws SQLException {
    String sql = "Update users set money = money - ? where keycloak_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, moneyAmount);
      preparedStatement.setString(2, keycloakId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void refundRequest(String keycloakId, Double moneyAmount) throws SQLException {
    String sql = "Update users set money = money + ? where keycloak_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, moneyAmount);
      preparedStatement.setString(2, keycloakId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

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

  public int getUserTicketQuantity(String keycloakId, String gameName) throws SQLException {
    String sql = "Select quantity from user_tickets " +
        "where user_id = (SELECT id from users where keycloak_id = ?) " +
        "AND ticket_id = (SELECT id from tickets where name = ?)";
    int ticketNumber = 0;

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, keycloakId);
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

  public void createUserTicketsRecord(String keycloakId, String gameName, int ticketNumbers) {
    String sql = "INSERT INTO user_tickets (user_id, ticket_id, quantity) " +
        "SELECT u.id, t.id, ? from users u, tickets t where u.keycloak_id = ? and t.name = ?" +
        "ON DUPLICATE KEY UPDATE user_tickets.quantity = user_tickets.quantity + VALUES(quantity)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setDouble(1, ticketNumbers);
      preparedStatement.setString(2, keycloakId);
      preparedStatement.setString(3, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void updateUserTicketsNumber(String keycloakId, String gameName, int ticketNumbers) {
    String sql = "Update user_tickets set quantity = quantity - ? " +
        "where user_id = (SELECT id from users where keycloak_id = ?)" +
        "AND ticket_id = (SELECT id from tickets where name = ?)";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, ticketNumbers);
      preparedStatement.setString(2, keycloakId);
      preparedStatement.setString(3, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void deleteUserTicketsRecord(String keycloakId, String gameName) {
    String sql = "DELETE from user_tickets where user_id = (SELECT id from users where keycloak_id = ?) " +
        "AND ticket_id = (SELECT id from tickets where name = ?)";

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, keycloakId);
      preparedStatement.setString(2, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}