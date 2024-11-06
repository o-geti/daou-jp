package com.minsu.kim.daoujapan.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author minsu.kim
 * @since 1.0
 */
public class Aes256Helper {

  private Aes256Helper() {}

  private static final String SECRET_KEY = "TEST_APPLICATION";
  private static final String ALGORITHM = "AES";

  public static String encrypt(String value) throws Exception {
    SecretKeySpec key = generateKey(SECRET_KEY);
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

    return Base64.getEncoder().encodeToString(encryptedValue);
  }

  public static String decrypt(String value) throws Exception {
    SecretKeySpec key = generateKey(SECRET_KEY);
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decodedValue = Base64.getDecoder().decode(value);
    byte[] decryptedValue = cipher.doFinal(decodedValue);

    return new String(decryptedValue, StandardCharsets.UTF_8);
  }

  private static SecretKeySpec generateKey(String secret) throws Exception {
    byte[] key = secret.getBytes(StandardCharsets.UTF_8);
    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    key = sha.digest(key);

    return new SecretKeySpec(key, ALGORITHM);
  }
}
