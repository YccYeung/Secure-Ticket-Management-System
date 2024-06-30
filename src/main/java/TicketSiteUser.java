import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a user in the Ticket Management System.
 *
 * This class provides various functionalities for the user, including accessing the user dashboard,
 * managing user profiles, depositing money, and interacting with tickets.
 *
 */
public class TicketSiteUser {
  private String password;
  private String username;
  TicketSystemBackend ticketSystemBackend;
  TicketSystemDB ticketSystemDB;

  final String ANSI_RESET = "\u001B[0m";
  final String ANSI_CYAN = "\u001B[36m";
  final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
  final String ANSI_BRIGHT_GREEN = "\u001B[92m";
  final String ANSI_BRIGHT_RED = "\u001B[91m";
  final String ANSI_BRIGHT_WHITE = "\u001B[97m";

  /**
   * Creates a new TicketSiteUser with the given username, password, and card number. They do
   * NOT have a ticket and are NOT logged in.
   * @param username the username of this user
   * @param password the password of this user
   * @throws IllegalArgumentException if the card number is not 16 digits long
   */
  public TicketSiteUser(String username, String password) throws SQLException {
    this.username = username;
    this.password = password;
    this.ticketSystemBackend = new TicketSystemBackend();
    this.ticketSystemDB = new TicketSystemDB();
  }

  public void userDashBoard() {
    Scanner scanner = new Scanner(System.in);
    int userChoice = -1;

    while (userChoice != 0) {
      userDashBoardMenu();
      try {
        userChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (userChoice) {
          case 1:
            moneyDeposit();
            break;
          case 2:
            printGameSchedule();
            break;
          case 3:
            buyTickets();
            break;
          case 4:
            sellTickets();
            break;
          case 5:
            ListCurrentTicket();
            break;
          case 0:
            System.out.println(" ");
            System.out.println("╭──────────────────────────────────────────╮");
            System.out.println("│                                          │");
            System.out.println("│           Logout Successfully            │");
            System.out.println("│                                          │");
            System.out.println("│         Returning to main menu...        │");
            System.out.println("│                                          │");
            System.out.println("╰──────────────────────────────────────────╯");
            break;
          default:
            System.out.println("Not a valid command, please try again");
        }
      } catch (InputMismatchException e) {
        System.out.println("Not a valid command, please enter a number.");
        scanner.next(); // consume invalid input
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Displays the user dashboard menu.
   *
   * This method prints out the options available for the user to interact with the system from their dashboard.
   * It provides various actions such as logging out, depositing money, displaying the game schedule, buying tickets,
   * selling tickets, and listing current tickets held.
   *
   * The menu is displayed in a formatted and visually appealing manner, with color coding for better user experience.
   */
  public void userDashBoardMenu() {
    System.out.println("\n");
    System.out.println("===================================================");
    System.out.println("|                                                 |");
    System.out.println("|               \u001B[1m\u001B[34mUSER DASHBOARD\u001B[0m                    |");
    System.out.println("|                                                 |");
    System.out.println("===================================================");
    System.out.println("| \u001B[33mPlease enter a number to select an action:\u001B[0m      |");
    System.out.println("|                                                 |");
    System.out.println("|  \u001B[32m0:\u001B[0m Logout and Return to Main Menu              |");
    System.out.println("|  \u001B[32m1:\u001B[0m Deposit Money to Account                    |");
    System.out.println("|  \u001B[32m2:\u001B[0m Display UW-Madison Football Games Schedule  |");
    System.out.println("|  \u001B[32m3:\u001B[0m Buy Tickets                                 |");
    System.out.println("|  \u001B[32m4:\u001B[0m Sell Tickets                                |");
    System.out.println("|  \u001B[32m5:\u001B[0m List Current Tickets Held                   |");
    System.out.println("|                                                 |");
    System.out.println("===================================================");
    TicketSystemFrontend.printFancyPrompt();
  }

  /**
   * Handles the process of depositing money into the user's account.
   *
   * This method prompts the user to enter their credit card number for verification.
   * If the credit card number matches the user's stored information, the user is then
   * prompted to enter a deposit amount. The deposit amount must be between $1 and $999.
   * If the amount is valid, it is added to the user's account balance.
   *
   * The method provides visual feedback for the different stages of the deposit process,
   * including successful deposits and error messages for invalid inputs.
   *
   * @throws SQLException If a database access error occurs.
   */
  public void moneyDeposit() throws SQLException {
    Scanner scanner = new Scanner(System.in);
    System.out.println("╭──────────────────────────────────────────────╮");
    System.out.println("│ Please Enter your Credit Card Number:        │");
    System.out.print("╰─➤ ");
    String inputCardNumber = scanner.next();

    if (ticketSystemDB.creditCardVerify(this.username, inputCardNumber)) {
      System.out.println("╭──────────────────────────────────────────────╮");
      System.out.println("│ Select deposit amount:                       │");
      System.out.print("╰─➤ ");
      double moneyAmount = scanner.nextDouble();
      if (moneyAmount < 1 || moneyAmount > 999) {
        System.out.println("╭────────────────────────────────────────────────────────────────╮");
        System.out.println("│ ✖️ Deposit cannot be less than $1 or more than $999 each time  │");
        System.out.println("╰────────────────────────────────────────────────────────────────╯");
      } else {
        ticketSystemDB.depositMoney(this.username, moneyAmount);
        System.out.println("╭────────────────────────────────────╮");
        System.out.printf("│ ✅  Deposit $%.2f successfully!    │%n", moneyAmount);
        System.out.println("╰────────────────────────────────────╯");
      }
    } else {
      System.out.println("╭───────────────────────────────────────╮");
      System.out.println("│ ✖️ Credit Card Number doesn't match!  │");
      System.out.println("╰───────────────────────────────────────╯");
    }
  }

  /**
   * Prints the game schedule.
   *
   * This method serves as a wrapper to call the `gameSchedule` method from the `ticketSystemDB` instance.
   * It prints a blank line before calling the `gameSchedule` method to ensure proper formatting.
   *
   */
  public void printGameSchedule() {
    System.out.println(" ");
    this.ticketSystemDB.gameSchedule();
  }

  /**
   * Handles the process of buying tickets for a game.
   *
   * This method guides the user through selecting a game, specifying the number of tickets,
   * verifying ticket availability and account balance, processing payment, and updating the database.
   * It also prints a transaction receipt for the user's record.
   *
   * The method performs the following steps:
   *
   *   Displays the game schedule.
   *   Prompts the user to select a game and specify the number of tickets to purchase.
   *   Verifies ticket availability.
   *   Calculates the total cost of the tickets and verifies the user's account balance.
   *   Retrieves the user's credit card number and processes the payment via the PaymentGateway.
   *   Updates the ticket quantity in the database and creates a record of the user's purchase.
   *   Prints a transaction receipt for the user.
   *
   * @throws Exception If any error occurs during the ticket purchase process.
   */
  public void buyTickets() throws Exception {
    PaymentGateway paymentGateway = new PaymentGateway();
    Scanner scanner = new Scanner(System.in);

    // 1. show game schedule
    printGameSchedule();
    System.out.println(ANSI_CYAN + "╭───────────────────────────────────────────────────────────╮");
    System.out.println("│ " + ANSI_BRIGHT_YELLOW + "Please select the game ticket you would like to purchase:" + ANSI_CYAN + " │");
    System.out.print("╰─➤ " + ANSI_RESET);
    String gameName = scanner.nextLine();
    if (!ticketSystemDB.gameNameAllowList.containsKey(gameName)) {
      System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "There is no game with the name: " + ANSI_BRIGHT_YELLOW + gameName + ANSI_CYAN + "              │");
      System.out.println("╰────────────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }
    System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────────────╮");
    System.out.println("│ " + ANSI_BRIGHT_YELLOW + "Please select how many tickets you would like to purchase:" + ANSI_CYAN + " │");
    System.out.print("╰─➤ " + ANSI_RESET);
    String userInputNumber = scanner.nextLine();
    try {
      Integer.parseInt(userInputNumber);
    } catch (Exception e) {
      System.out.println(ANSI_CYAN + "╭──────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "Invalid input format" + ANSI_CYAN + "                         │");
      System.out.println("╰──────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }
    int ticketNumber = Integer.parseInt(userInputNumber);

    // 2. check if there is enough tickets to purchase
    if (!ticketSystemDB.ticketQuantityVerify(gameName, ticketNumber)) {
      System.out.println(ANSI_CYAN + "╭──────────────────────────────────────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "Not enough tickets available. Please wait until tickets are available again." + ANSI_CYAN + " │");
      System.out.println("╰──────────────────────────────────────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }

    // 3. calculate ticket costs
    double ticketCost = ticketSystemDB.ticketTotalCost(gameName, ticketNumber);
    if (!ticketSystemDB.checkAccountBalance(this.username, ticketCost)) {
      System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "Insufficient funds in your account. Please deposit more money." + ANSI_CYAN + " │");
      System.out.println("╰────────────────────────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }

    // 4. get credit card number plaintext
    String cardNumber = ticketSystemDB.getCardNumber(this.username);

    // 5. connect to PaymentGateway
    String cardNumberToken  = paymentGateway.cardNumberToken(cardNumber);
    paymentGateway.processPayment(this.username, cardNumberToken, ticketCost);
    ticketSystemDB.updateTicketQuantity(gameName, -Math.abs(ticketNumber));
    ticketSystemDB.createUserTicketsRecord(username, gameName, ticketNumber);

    // 6. print transaction receipt for user record
    printPurchaseReceipt(gameName, ticketNumber, ticketCost);
  }

  /**
   * Handles the process of selling tickets.
   *
   * This method guides the user through selecting a ticket to sell, specifying the number of tickets,
   * verifying the user's ticket holdings, processing the refund, and updating the database.
   * It also prints a sales receipt for the user's record.
   *
   * The method performs the following steps:
   *
   *   Displays the user's current ticket holdings.
   *   Prompts the user to select a ticket to sell and specify the number of tickets to sell.
   *   Verifies the user's ticket holdings.
   *   Calculates the total value of the tickets to be sold.
   *   Retrieves the user's credit card number and processes the refund via the PaymentGateway.
   *   Updates the ticket quantity in the database and adjusts the user's ticket records.
   *   Prints a sales receipt for the user.
   *
   * @throws Exception If any error occurs during the ticket sale process.
   */
  public void sellTickets() throws Exception {
    Scanner scanner = new Scanner(System.in);
    PaymentGateway paymentGateway = new PaymentGateway();

    ticketSystemDB.listCurrentTicketsHolding();
    System.out.println(ANSI_CYAN + "╭──────────────────────────────────────────────╮");
    System.out.println("│ " + ANSI_BRIGHT_YELLOW + "Which ticket you would like to sell:" + ANSI_CYAN + "         │");
    System.out.print("╰─➤ " + ANSI_RESET);
    String ticketToSell = scanner.nextLine();
    if (!ticketSystemDB.availableToSellList.containsKey(ticketToSell)) {
      System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "There is no game with the name: " + ANSI_BRIGHT_YELLOW + ticketToSell + ANSI_CYAN + "              │");
      System.out.println("╰────────────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }
    System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────────────╮");
    System.out.println("│ " + ANSI_BRIGHT_YELLOW + "Please select how many tickets you would like to sell:" + ANSI_CYAN + "     │");
    System.out.print("╰─➤ " + ANSI_RESET);
    String userInputNumber = scanner.nextLine();
    try {
      Integer.parseInt(userInputNumber);
    } catch (Exception e) {
      System.out.println(ANSI_CYAN + "╭──────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "Invalid input format" + ANSI_CYAN + "                         │");
      System.out.println("╰──────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }
    int ticketNumber = Integer.parseInt(userInputNumber);
    if (ticketNumber > ticketSystemDB.getUserTicketQuantity(username, ticketToSell)) {
      System.out.println(ANSI_CYAN + "╭──────────────────────────────────────────────────────╮");
      System.out.println("│ " + ANSI_BRIGHT_RED + "✖ " + ANSI_BRIGHT_WHITE + "Not enough tickets to sell" + ANSI_CYAN + "                         │");
      System.out.println("╰──────────────────────────────────────────────────────╯" + ANSI_RESET);
      return;
    }

    // 3. calculate ticket costs
    double ticketCost = ticketSystemDB.ticketTotalCost(ticketToSell, ticketNumber);

    // 4. connect to paymentGateway
    String cardNumber = ticketSystemDB.getCardNumber(this.username);
    String cardNumberToken  = paymentGateway.cardNumberToken(cardNumber);
    paymentGateway.refundPayment(this.username, cardNumberToken, ticketCost);
    ticketSystemDB.updateTicketQuantity(ticketToSell, Math.abs(ticketNumber));
    if (ticketNumber - ticketSystemDB.getUserTicketQuantity(this.username, ticketToSell) == 0) {
      ticketSystemDB.deleteUserTicketsRecord(username, ticketToSell);
    } else {
      ticketSystemDB.updateUserTicketsNumber(username, ticketToSell, ticketNumber);
    }
    // 5. print sales receipt for user record
    printSellReceipt(ticketToSell, ticketNumber, ticketCost);
  }

  /**
   * Lists the current tickets held by the user.
   *
   * This method calls the backend to display the user's current ticket holdings.
   *
   */
  public void ListCurrentTicket() {
    ticketSystemDB.listCurrentTicketsHolding();
  }

  /**
   * Prints a purchase receipt for the user.
   *
   * This method prints a formatted receipt for the user, including details such as the game name,
   * number of tickets purchased, total cost, and date of purchase.
   *
   * @param gameName The name of the game for which the tickets were purchased.
   * @param tickNumbers The number of tickets purchased.
   * @param totalCost The total cost of the tickets.
   */
  public void printPurchaseReceipt(String gameName, int tickNumbers, double totalCost) {

    // Get the current date and time
    java.util.Date date = new java.util.Date();
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Print the fancy receipt
    System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────╮");
    System.out.println("│                                                    │");
    System.out.println("│          " + ANSI_BRIGHT_GREEN + "✨ TICKET PURCHASE RECEIPT ✨" + ANSI_CYAN + "            │");
    System.out.println("│                                                    │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Game Name: " + ANSI_BRIGHT_YELLOW + String.format("%-36s", gameName) + ANSI_CYAN + "    │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Number of Tickets: " + ANSI_BRIGHT_YELLOW + String.format("%-29d", tickNumbers) + ANSI_CYAN + "   │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Total Cost: " + ANSI_BRIGHT_YELLOW + String.format("$%-35.2f", totalCost) + ANSI_CYAN + "   │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Date of Purchase: " + ANSI_BRIGHT_YELLOW + String.format("%-27s", formatter.format(date)) + ANSI_CYAN + "      │");
    System.out.println("│                                                    │");
    System.out.println("│          " + ANSI_BRIGHT_GREEN + "Thank you for your purchase!" + ANSI_CYAN + "              │");
    System.out.println("│                                                    │");
    System.out.println("╰────────────────────────────────────────────────────╯" + ANSI_RESET);
  }

  /**
   * Prints a sale receipt for the user.
   *
   * This method prints a formatted receipt for the user, including details such as the game name,
   * number of tickets sold, total earned, and date of sale.
   *
   * @param gameName The name of the game for which the tickets were sold.
   * @param tickNumbers The number of tickets sold.
   * @param totalCost The total amount earned from the sale.
   */
  public void printSellReceipt(String gameName, int tickNumbers, double totalCost) {

    // Get the current date and time
    java.util.Date date = new java.util.Date();
    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Print the fancy receipt
    System.out.println(ANSI_CYAN + "╭────────────────────────────────────────────────────╮");
    System.out.println("│                                                    │");
    System.out.println("│          " + ANSI_BRIGHT_GREEN + "✨ TICKET SALE RECEIPT ✨" + ANSI_CYAN + "                 │");
    System.out.println("│                                                    │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Game Name: " + ANSI_BRIGHT_YELLOW + String.format("%-36s", gameName) + ANSI_CYAN + "    │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Number of Tickets: " + ANSI_BRIGHT_YELLOW + String.format("%-29d", tickNumbers) + ANSI_CYAN + "   │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Total Earned: " + ANSI_BRIGHT_YELLOW + String.format("$%-35.2f", totalCost) + ANSI_CYAN + " │");
    System.out.println("│ " + ANSI_BRIGHT_WHITE + "Date of Sale: " + ANSI_BRIGHT_YELLOW + String.format("%-27s", formatter.format(date)) + ANSI_CYAN + "          │");
    System.out.println("│                                                    │");
    System.out.println("│          " + ANSI_BRIGHT_GREEN + "Thank you for selling with us!" + ANSI_CYAN + "            │");
    System.out.println("│                                                    │");
    System.out.println("╰────────────────────────────────────────────────────╯" + ANSI_RESET);
  }

}
