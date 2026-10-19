package com.skillchain.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * ============================================================================
 * SECURITY UTILITY: SHA-256 PASSWORD HASHING WITH CRYPTOGRAPHIC SALT
 * ============================================================================
 * Uses standard Java built-in cryptography (no external libraries).
 * - SecureRandom generates a unique 16-byte cryptographically secure salt.
 * - MessageDigest computes the SHA-256 digest of salt + password.
 * - HexFormat (JDK 17+) converts bytes to hexadecimal representation.
 * - MessageDigest.isEqual provides constant-time comparison to protect
 *   against side-channel timing attacks.
 */
public final class PasswordUtil {

    private static final int SALT_BYTE_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Private constructor to prevent instantiation of utility class
    private PasswordUtil() {
    }

    /**
     * Generates a cryptographically strong 16-byte random salt as a 32-character hex string.
     *
     * @return Hex-encoded salt string.
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /**
     * Computes the SHA-256 hash of a password concatenated with the provided salt.
     *
     * @param plainPassword Plaintext password entered by the user
     * @param saltHex       Hex-encoded salt
     * @return 64-character lowercase hexadecimal SHA-256 hash
     */
    public static String hashPassword(String plainPassword, String saltHex) {
        if (plainPassword == null || saltHex == null) {
            throw new IllegalArgumentException("Password and salt must not be null.");
        }

        try {
            byte[] saltBytes = HexFormat.of().parseHex(saltHex);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Feed salt bytes into digest first
            digest.update(saltBytes);
            // Digest the password bytes using standard UTF-8 charset
            byte[] hashBytes = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable on current JVM", e);
        }
    }

    /**
     * Verifies whether an entered plaintext password matches the stored salt and hash.
     *
     * @param rawPassword      Candidate password provided during login
     * @param saltHex          Stored salt from the database
     * @param expectedHashHex  Stored hash from the database
     * @return true if password is valid; false otherwise
     */
    public static boolean verifyPassword(String rawPassword, String saltHex, String expectedHashHex) {
        if (rawPassword == null || saltHex == null || expectedHashHex == null) {
            return false;
        }

        String calculatedHash = hashPassword(rawPassword, saltHex);
        byte[] calculatedBytes = calculatedHash.getBytes(StandardCharsets.UTF_8);
        byte[] expectedBytes = expectedHashHex.getBytes(StandardCharsets.UTF_8);

        // Constant-time comparison prevents timing attacks
        return MessageDigest.isEqual(calculatedBytes, expectedBytes);
    }
}
