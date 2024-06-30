import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Provides AES encryption and decryption utilities.
 * This class includes methods for generating AES keys, encoding and decoding keys,
 * and encrypting and decrypting data using AES encryption.
 */
public class AESEncryption {

  /**
   * Generates an AES secret key of the specified size.
   *
   * @param keySize The size of the AES key to generate.
   * @return The generated AES secret key.
   * @throws Exception If an error occurs during key generation.
   */
    public static SecretKey GenerateAESKey(int keySize) throws Exception {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(keySize);
      return keyGenerator.generateKey();
    }

  /**
   * Encodes the provided AES secret key to a Base64 string.
   *
   * @param secretKey The AES secret key to encode.
   * @return The Base64-encoded string representation of the key.
   */
    public static String encodeKey(SecretKey secretKey) {
      return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

  /**
   * Decodes the provided Base64 string to an AES secret key.
   *
   * @param encodedKey The Base64-encoded string representation of the key.
   * @return The decoded AES secret key.
   */
    public static SecretKey decodeKey(String encodedKey) {
      byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
      return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

  /**
   * Encrypts the provided data using AES encryption with the specified key.
   *
   * @param data The data to encrypt.
   * @param key The AES key to use for encryption.
   * @return The Base64-encoded string representation of the encrypted data.
   * @throws Exception If an error occurs during encryption.
   */
    public static String encrypt(String data, SecretKey key) throws Exception{
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(new byte[16]);
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);
      byte[] encryptData = cipher.doFinal(data.getBytes());
      return Base64.getEncoder().encodeToString(encryptData);
    }

  /**
   * Decrypts the provided encrypted data using AES encryption with the specified key.
   *
   * @param encryptData The Base64-encoded string representation of the encrypted data.
   * @param key The AES key to use for decryption.
   * @return The decrypted data as a string.
   * @throws Exception If an error occurs during decryption.
   */
    public static String decrypt(String encryptData, SecretKey key) throws Exception{
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(new byte[16]);
      cipher.init(Cipher.DECRYPT_MODE, key, iv);
      byte[] decryptedData = Base64.getDecoder().decode(encryptData);
      return new String(cipher.doFinal(decryptedData));
    }
}
