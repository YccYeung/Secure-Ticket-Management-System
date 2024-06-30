import org.springframework.security.crypto.bcrypt.BCrypt;
import javax.crypto.SecretKey;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Provides database access and operations for the Ticket Management System.
 * This class handles connections to the database, executing queries, and managing
 * game and ticket data.
 *
 */
public class TicketSystemDB {

  private final String JDBC_URL = "jdbc:mysql://localhost:3306/";
  private final String DBNAME = "SystemDB";
  private final String jdbcUrlWithDatabase = JDBC_URL + DBNAME;
  private final String USER = "Justin";
  private final String PASSWORD = "123456";
  private final String secretKey = "wK6XNsTzLHxknM7XsB4a9w==";
  public HashMap<String, Integer> gameNameAllowList = new HashMap<>();
  public HashMap<String, Integer> availableToSellList = new HashMap<>();

  /**
   * Constructs a TicketSystemDB object and initializes the database.
   * This constructor creates the database and necessary tables if they do not already exist.
   *
   * @throws SQLException If a database access error occurs.
   */
  public TicketSystemDB() throws SQLException {createDatabase();createTable();}

  /**
   * Creates the database if it does not already exist.
   *
   * @throws SQLException if a database access error occurs
   */
  public void createDatabase() throws SQLException {
    Connection connection = null;
    Statement statement = null;

    try {
      connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
      statement = connection.createStatement();
      String sql = "CREATE DATABASE IF NOT EXISTS " + DBNAME;
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (statement != null) statement.close();
        if (connection != null) connection.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Creates the users table if it does not already exist.
   *
   * @throws SQLException if a database access error occurs
   */
  public void createTable() throws SQLException {
    Connection connection = null;
    Statement statement = null;

    try {
      connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
      statement = connection.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS users (" +
          "id INT AUTO_INCREMENT PRIMARY KEY, " +
          "username VARCHAR(25) NOT NULL, " +
          "password VARCHAR(100) NOT NULL, " +
          "cardNumber VARCHAR(100) NOT NULL, " +
          "money int NOT NULL)";
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      if (statement != null) statement.close();
      if (connection != null) connection.close();
    }
  }

  /**
   * Creates a new user in the database with a hashed password and card number.
   *
   * @param username the username of the new user
   * @param password the password of the new user
   * @param cardNumber the card number of the new user
   * @throws SQLException if a database access error occurs
   */
  public void createUser(String username, String password, String cardNumber) throws SQLException{
    String sql = "INSERT INTO users (username, password, cardNumber, money) VALUES (?,?,?,?) ";
    String encryptedCardNumber = "";
    try {
      SecretKey key = AESEncryption.decodeKey(secretKey);
      encryptedCardNumber = AESEncryption.encrypt(cardNumber, key);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    try {
      Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
      PreparedStatement preparedStatement = connection.prepareStatement(sql);

      preparedStatement.setString(1, username);
      preparedStatement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
      preparedStatement.setString(3, encryptedCardNumber);
      preparedStatement.setInt(4, 0);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Verifies if a user with the specified username exists in the database.
   *
   * @param username the username to verify
   * @return true if the user exists, false otherwise
   */
  public boolean userVerify(String username) {
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "SELECT * from users where username = ?";

    try {
      Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, username);

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
   * Verifies the provided password for the specified username against the stored password in the database.
   *
   * This method connects to the database, retrieves the stored password for the given username, and compares it
   * with the provided password using BCrypt hashing.
   *
   * The method returns true if the provided password matches the stored password, otherwise false.
   *
   * @param username The username whose password is to be verified.
   * @param password The password to be verified.
   * @return true if the provided password matches the stored password, false otherwise.
   */
  public boolean passwordVerify(String username, String password) {
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "SELECT password from users where username = ?";

    try {
      Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, username);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String storedPassword = resultSet.getString("password");
          return BCrypt.checkpw(password, storedPassword);
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
      Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String sql = "select cardNumber from users where username = ?";
    String plainCardNumber = "";
    try  {
      Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Update users set money = money + ? where username = ?";
    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Select money from users where username = ?";

    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Select quantity from tickets where name = ?";

    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Select price from tickets where name = ?";
    double totalCost = 0.0;

    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Update users set money = money - ? where username = ?";
    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Update users set money = money + ? where username = ?";
    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Update tickets set quantity = quantity + ? where name = ?";
    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Select quantity from user_tickets " +
        "where user_id = (SELECT id from users where username = ?) " +
        "AND ticket_id = (SELECT id from tickets where name = ?)";
    int ticketNumber = 0;

    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "INSERT INTO user_tickets (user_id, ticket_id, quantity) " +
        "SELECT u.id, t.id, ? from users u, tickets t where u.username = ? and t.name = ?" +
        "ON DUPLICATE KEY UPDATE user_tickets.quantity = user_tickets.quantity + VALUES(quantity)";
    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "Update user_tickets set quantity = quantity - ? " +
        "where user_id = (SELECT id from users where username = ?)" +
        "AND ticket_id = (SELECT id from tickets where name = ?)";

    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
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
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "DELETE from user_tickets where user_id = (SELECT id from users where username = ?) " +
        "AND ticket_id = (SELECT id from tickets where name = ?)";

    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, gameName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Lists the current tickets held by the user.
   * This method connects to the database and retrieves the user's current ticket holdings,
   * displaying them in a formatted table with ANSI colors.
   */
  public void listCurrentTicketsHolding() {
    String jdbcUrlWithDatabase = JDBC_URL + DBNAME + "?serverTimezone=UTC&useSSL=false";
    String sql = "SELECT t.name, t.price, s.quantity from tickets t " +
        "INNER JOIN user_tickets s ON s.ticket_id = t.id " +
        "INNER JOIN users u ON s.user_id = u.id";
    try (Connection connection = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      ResultSet resultSet = preparedStatement.executeQuery();

      // ANSI escape codes for colors
      final String ANSI_RESET = "\u001B[0m";
      final String ANSI_CYAN = "\u001B[36m";
      final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
      final String ANSI_BRIGHT_GREEN = "\u001B[92m";
      final String ANSI_BRIGHT_RED = "\u001B[91m";
      final String ANSI_BRIGHT_WHITE = "\u001B[97m";

      // Print header
      System.out.println(ANSI_CYAN + "╭───────────────────────────────────────────────────────────────────────────╮");
      System.out.println("│                          " + ANSI_BRIGHT_GREEN + "Current Tickets Holding" + ANSI_CYAN + "                          │");
      System.out.println("├───────────────────────────────────────────────────────────────────────────┤");
      System.out.printf("│ %-30s │ %-11s │ %-10s │ %-13s │%n", "Game Name", "Price", "Quantity", "Total Cost");
      System.out.println("├───────────────────────────────────────────────────────────────────────────┤");

      boolean hasTickets = false;
      while (resultSet.next()) {
        hasTickets = true;
        String gameName = resultSet.getString("name");
        double price = resultSet.getDouble("price");
        int quantity = resultSet.getInt("quantity");
        double totalCost = price * quantity;
        availableToSellList.put(gameName, 1);

        // Print each ticket
        System.out.printf("│ %-30s │ $%-10.2f │ %-10d │ $%-12.2f │%n", gameName, price, quantity, totalCost);
      }

      if (!hasTickets) {
        System.out.println("│                  " + ANSI_BRIGHT_RED + "No tickets currently held." + ANSI_CYAN + "                          │");
      }

      // Print footer
      System.out.println("╰───────────────────────────────────────────────────────────────────────────╯" + ANSI_RESET);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Displays the game schedule in a formatted table.
   * This method connects to the database, retrieves the game schedule,
   * and displays it in a neatly formatted table with color coding for home and road games.
   */
  public void gameSchedule() {
    String query = "SELECT name, location, price, event_date FROM tickets ORDER BY event_date";
    try (Connection conn = DriverManager.getConnection(jdbcUrlWithDatabase, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

      System.out.println("╔═════════════════════════════════════════════════════════════════════════════════════════╗");
      System.out.println("║                                   \u001B[1mGAME SCHEDULE\u001B[0m                                         ║");
      System.out.println("╠═══════════════════════════════════╦════════════════════════╦══════════════╦═════════════╣");
      System.out.printf("║ %-33s ║ %-22s ║ %-12s ║ %-11s ║\n", "Game", "Location", "Date", "Price");
      System.out.println("╠═══════════════════════════════════╬════════════════════════╬══════════════╬═════════════╣");

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      while (rs.next()) {
        String name = rs.getString("name");
        String location = rs.getString("location");
        String eventDate = dateFormat.format(rs.getTimestamp("event_date"));
        String price = String.format("$%.2f", rs.getDouble("price"));
        gameNameAllowList.put(name, 1);

        // Apply color based on location
        if ("Camp Randall Stadium".equals(location)) {
          // Home game in green color
          System.out.printf("\u001B[32m║ %-33s ║ %-22s ║ %-12s ║ %-11s ║\u001B[0m\n", name, location, eventDate, price);
        } else {
          // Road game in red color
          System.out.printf("\u001B[31m║ %-33s ║ %-22s ║ %-12s ║ %-11s ║\u001B[0m\n", name, location, eventDate, price);
        }
      }

      System.out.println("╚═══════════════════════════════════╩════════════════════════╩══════════════╩═════════════╝");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }


}
