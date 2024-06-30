import javax.crypto.SecretKey;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Handles payment processing for the Ticket Management System.
 * This class provides methods for tokenizing card numbers, processing payments, and handling refunds.
 */
public class PaymentGateway {

  private final String secretKey = "wK6XNsTzLHxknM7XsB4a9w==";
  private HashMap<String, String> transaction = new HashMap<>();
  TicketSystemDB ticketSystemDB = new TicketSystemDB();

  /**
   * Constructs a PaymentGateway object and initializes the ticket system database.
   *
   * @throws SQLException If a database access error occurs.
   */
  public PaymentGateway() throws SQLException {}

  /**
   * Generates a token for the provided card number by encrypting it.
   * This method encrypts the card number using AES encryption and generates a unique token for it.
   *
   * @param cardNumber The credit card number to be tokenized.
   * @return The generated token for the card number.
   * @throws Exception If an error occurs during encryption.
   */
  public String cardNumberToken(String cardNumber) throws Exception {
    SecretKey key = AESEncryption.decodeKey(secretKey);
    String encryptedCardNumber = AESEncryption.encrypt(cardNumber, key);
    String token = "token_" + transaction.size();
    transaction.put(token, encryptedCardNumber);
    return token;
  }

  /**
   * Processes a payment for the specified user.
   * This method retrieves the encrypted card number associated with the provided token,
   * and deducts the specified amount from the user's account balance.
   *
   * @param username The username of the user making the payment.
   * @param token The token representing the card number.
   * @param moneyAmount The amount to be deducted from the user's account.
   * @throws SQLException If a database access error occurs.
   */
  public void processPayment(String username, String token, double moneyAmount)
      throws SQLException {
    String encryptedCard = transaction.get(token);
    if (token == null || encryptedCard == null) {
      System.out.println("Invalid Token");
      return;
    }
    ticketSystemDB.purchaseRequest(username, moneyAmount);
  }

  /**
   * Processes a refund for the specified user.
   * This method retrieves the encrypted card number associated with the provided token,
   * and adds the specified amount to the user's account balance.
   *
   * @param username The username of the user receiving the refund.
   * @param token The token representing the card number.
   * @param moneyAmount The amount to be added to the user's account.
   * @throws SQLException If a database access error occurs.
   */
  public void refundPayment(String username, String token, double moneyAmount)
      throws SQLException {
    String encryptedCard = transaction.get(token);
    if (token == null || encryptedCard == null) {
      System.out.println("Invalid Token");
      return;
    }
    ticketSystemDB.refundRequest(username, moneyAmount);
  }
}
