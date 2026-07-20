package com.bhahi.hrmodule.Utils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

//AES Utils
@Component
public class AESUtils {

    @Getter
    private static String password;


    @Value("${aes.secret.key}")
    @SuppressWarnings("squid:S2696")
    public void setPassword(
            String injectedPassword) { // Spring calls this
        AESUtils.password = injectedPassword;
    }

    // AES parameters
    private static final int GCM_IV_LENGTH = 12; // 12-byte IV recommended
    private static final int GCM_TAG_LENGTH = 128; // 128-bit authentication tag
    private static final int PBKDF2_ITERATIONS = 51159; // For key derivation
    private static final int PBKDF2_KEY_LENGTH = 256;

    private static final SecureRandom secureRandom = new SecureRandom();

    // ================================================
    // Encrypt a plaintext string using AES-GCM
    // Generates a random IV for each encryption
    // Returns Base64-encoded string: IV + ciphertext
    // ================================================
    public static String encrypt(String plainText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        // Generate random 12-byte IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        // Derive AES key from password
        SecretKey key = deriveKey(password, iv); // using IV as salt

        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        // Encrypt plaintext
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Prepend IV to ciphertext
        byte[] ivAndCipher = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, ivAndCipher, 0, iv.length);
        System.arraycopy(cipherText, 0, ivAndCipher, iv.length, cipherText.length);

        // Base64 encode for easy storage/transmission
        return  Base64.getUrlEncoder().withoutPadding().encodeToString(ivAndCipher);

    }

    // ================================================
    // Decrypt a Base64-encoded ciphertext string
    // Extracts IV from first 12 bytes
    // Returns original plaintext
    // ================================================
    public static String decrypt(String base64CipherText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ivAndCipher = Base64.getUrlDecoder().decode(base64CipherText);

        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(ivAndCipher, 0, iv, 0, GCM_IV_LENGTH);

        // Extract ciphertext
        byte[] cipherText = new byte[ivAndCipher.length - GCM_IV_LENGTH];
        System.arraycopy(ivAndCipher, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

        // Derive AES key from password
        SecretKey key = deriveKey(password, iv); // same IV used as salt

        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        // Decrypt
        byte[] plainBytes = cipher.doFinal(cipherText);

        return  new String(plainBytes, StandardCharsets.UTF_8);

    }

    // ================================================
    // Derive AES key from password using PBKDF2 with HMAC-SHA256
    // Salt ensures unique key per encryption
    // ================================================
    private static SecretKey deriveKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }




    // Convert normal Base64 to URL-safe Base64 (without padding)
    public static String toUrlSafe(String base64) {
        return base64.replace('+', '-')
                .replace('/', '_')
                .replace("=", ""); // remove padding
    }

    // Convert URL-safe Base64 back to normal Base64 (with padding restored)
    public static String toNormalBase64(String urlSafe) {
        String base64 = urlSafe.replace('-', '+')
                .replace('_', '/');

        // Restore padding if needed (Base64 must be multiple of 4 chars)
        int padding = base64.length() % 4;
        if (padding > 0) {
            base64 += "=".repeat(4 - padding);
        }
        return base64;
    }
}