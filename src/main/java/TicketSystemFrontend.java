import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;
import static java.lang.System.exit;

/**
 * Frontend for Ticket Management System.
 * This class handles user interaction and input, providing a console-based menu
 * for users to login, register, and access other functionalities of the Ticket Management System.
 *
 */
public class TicketSystemFrontend {

  /**
   * Displays the user menu.
   * This method prints out the options available for the user to interact with the system.
   * It provides options to login, register as a new user, or exit the system.
   *
   */
  public static void userMenu() {
    System.out.println("\n");
    System.out.println("===================================================");
    System.out.println("|                                                 |");
    System.out.println("|               \u001B[1m\u001B[34mMAIN MENU\u001B[0m                         |");
    System.out.println("|                                                 |");
    System.out.println("===================================================");
    System.out.println("| \u001B[33mPlease enter a number to select an action:\u001B[0m      |");
    System.out.println("|                                                 |");
    System.out.println("|  \u001B[32m0:\u001B[0m Exit Ticket System                          |");
    System.out.println("|  \u001B[32m1:\u001B[0m Login to Ticket System                      |");
    System.out.println("|  \u001B[32m2:\u001B[0m Register for New User                       |");
    System.out.println("|                                                 |");
    System.out.println("===================================================");
    printFancyPrompt();
  }

  /**
   * Main method for the Ticket System Frontend.
   * This method initializes the user interface, handles user input, and calls appropriate backend methods
   * based on user choices. It runs a loop to continuously display the menu and process user input
   * until the user decides to exit the system.
   *
   * @param args Command line arguments.
   * @throws SQLException If there is a database access error.
   *
   */
  public static void main(String[] args) throws SQLException {
    Scanner scanner = new Scanner(System.in);
    int userChoice = -1;

    while (userChoice != 0) {
      userMenu();
      try {
        userChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (userChoice) {
          case 1:
            TicketSystemBackend.loginInterface();
            break;
          case 2:
            TicketSystemBackend.registerInterface();
            break;
          case 0:
            System.out.println(
                "\n===================================================");
            System.out.println("|                                                 |");
            System.out.println("|  Thank you for using our Ticket Management      |");
            System.out.println("|  System, see you next time!                     |");
            System.out.println("|                                                 |");
            System.out.println("===================================================");
            exit(0);
            break;
          default:
            System.out.println("\n[Error] Not a valid command, please try again.");
        }
      } catch (InputMismatchException e) {
        System.out.println("\n[Error] Not a valid command, please enter a number.");
        scanner.next(); // consume invalid input
      }
    }
  }

  /**
   * Prints a fancy input prompt.
   *
   * This method displays a visually appealing input prompt for the user to enter their choice.
   * The prompt is formatted with borders and colored text to enhance the user experience.
   *
   * The method uses ANSI escape codes to add color to the text.
   */
  public static void printFancyPrompt() {
    System.out.println("===================================================");
    System.out.print("|  \u001B[35mEnter your choice:\u001B[0m ");
  }
}
