package shush.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cryptographic helpers for Shush.
 * <p>
 * Implements AES-256-GCM authenticated encryption with keys derived from a master password
 * via PBKDF2-HMAC-SHA256. Each encryption uses a fresh random salt and IV.
 * </p>
 *
 * <h2>Payload format</h2>
 * The encoded string returned by {@link #encrypt(String, String)} is URL-safe Base64 (no padding)
 * over the following binary layout:
 * <pre>
 * [ 5 bytes magic "SV1\0" ][ 16 bytes salt ][ 12 bytes IV ][ N bytes ciphertext (includes GCM tag) ]
 * </pre>
 * Where:
 * <ul>
 *   <li>Magic/version: {@code "SV1\0"} (Shush Version 1)</li>
 *   <li>Salt: 16 random bytes for PBKDF2</li>
 *   <li>IV: 12 random bytes for AES-GCM</li>
 *   <li>Ciphertext: AES-GCM output of the UTF-8 plaintext</li>
 * </ul>
 *
 * <h2>Key derivation</h2>
 * PBKDF2WithHmacSHA256, 210,000 iterations, 256-bit key.
 * <p>
 * Note: Strings are immutable; consider using the {@code char[]} overloads to allow best-effort zeroing.
 * </p>
 */
public final class CryptoUtils {

    /** Magic/version marker: 'S','V','1','\0' */
    private static final byte[] MAGIC_V1 = new byte[]{0x53, 0x56, 0x31, 0x00}; // "SV1\0"
    private static final int SALT_LEN = 16;          // 128-bit salt for PBKDF2
    private static final int IV_LEN = 12;            // 96-bit IV for GCM
    private static final int KEY_BITS = 256;         // AES-256
    private static final int PBKDF2_ITERATIONS = 210_000;
    private static final int GCM_TAG_BITS = 128;     // 16-byte tag
    private static final SecureRandom RNG = new SecureRandom();

    private CryptoUtils() { /* no instances */ }

    /**
     * Encrypts a UTF-8 string using AES-256-GCM with a key derived from the provided master password.
     *
     * @param plaintext      the plaintext string (UTF-8)
     * @param masterPassword the master password
     * @return URL-safe Base64 (unpadded) encoded payload as described in class docs
     * @throws GeneralSecurityException if encryption fails
     */
    public static String encrypt(String plaintext, String masterPassword) throws GeneralSecurityException {
        if (plaintext == null) plaintext = "";
        if (masterPassword == null) throw new IllegalArgumentException("masterPassword cannot be null");
        return encrypt(plaintext.getBytes(StandardCharsets.UTF_8), masterPassword.toCharArray());
    }

    /**
     * Decrypts a payload produced by {@link #encrypt(String, String)} back into a UTF-8 string.
     *
     * @param payload        URL-safe Base64 (unpadded) encoded payload
     * @param masterPassword the master password
     * @return the decrypted plaintext (UTF-8)
     * @throws GeneralSecurityException if decryption fails or authentication fails
     */
    public static String decrypt(String payload, String masterPassword) throws GeneralSecurityException {
        if (payload == null) throw new IllegalArgumentException("payload cannot be null");
        if (masterPassword == null) throw new IllegalArgumentException("masterPassword cannot be null");
        byte[] out = decryptToBytes(payload, masterPassword.toCharArray());
        try {
            return new String(out, StandardCharsets.UTF_8);
        } finally {
            zero(out);
        }
    }

    /**
     * Encrypts arbitrary bytes using AES-256-GCM with a key derived from the master password.
     * Returns URL-safe Base64 (unpadded) encoded payload.
     *
     * @param data           plaintext bytes
     * @param masterPassword master password as {@code char[]} (best-effort zeroed)
     * @return encoded payload
     * @throws GeneralSecurityException if encryption fails
     */
    public static String encrypt(byte[] data, char[] masterPassword) throws GeneralSecurityException {
        if (data == null) data = new byte[0];
        if (masterPassword == null) throw new IllegalArgumentException("masterPassword cannot be null");

        byte[] salt = new byte[SALT_LEN];
        byte[] iv = new byte[IV_LEN];
        RNG.nextBytes(salt);
        RNG.nextBytes(iv);

        SecretKeySpec key = null;
        try {
            key = deriveKey(masterPassword, salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcm = new GCMParameterSpec(GCM_TAG_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcm);
            byte[] ct = cipher.doFinal(data);

            // Build binary payload: MAGIC | SALT | IV | CT
            ByteBuffer buf = ByteBuffer.allocate(MAGIC_V1.length + SALT_LEN + IV_LEN + c_
