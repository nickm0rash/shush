
package shush.vault;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages encrypted storage of password entries in a secure vault.
 * Uses AES-GCM encryption with PBKDF2-derived keys.
 */
public class VaultManager {
    private static final int GCM_TAG_LENGTH = 128; // Authentication tag length in bits for AES-GCM
    private static final int SALT_LENGTH = 16;     // Salt size for key derivation
    private static final int IV_LENGTH = 12;       // Initialization vector length for GCM
    private static final int ITERATIONS = 65536;   // PBKDF2 iteration count
    private static final int KEY_LENGTH = 256;     // AES key size in bits

    private Map<String, VaultEntry> vault = new HashMap<>();

    /**
    * Adds a new password entry to the in-memory vault.
    *
    * @param label         Unique identifier for the password entry
    * @param username      Username or email associated with the entry
    * @param password      Plaintext password (will be encrypted on save)
    * @param requires2FA   Whether this entry requires 2FA on access
    */
    public void addEntry(String label, String username, String password, boolean requires2FA) throws Exception {
        VaultEntry entry = new VaultEntry(username, password, requires2FA);
        vault.put(label, entry);
    }

    /**
    * Retrieves a password entry by label from the in-memory vault.
    *
    * @param label The identifier used to store the entry
    * @return VaultEntry object or null if not found
    */
    public VaultEntry getEntry(String label) {
        return vault.get(label);
    }

    /**
    * Encrypts and saves the in-memory vault to disk.
    *
    * @param filePath       Path to output encrypted vault file
    * @param masterPassword Master password for encryption
    * @throws Exception     If encryption or file write fails
    */
    public void saveVault(String filePath, char[] masterPassword) throws Exception {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, VaultEntry> e : vault.entrySet()) {
            json.append("\"").append(e.getKey()).append("\":")
                .append(e.getValue().toJson()).append(",");
        }
        if (json.length() > 1) json.setLength(json.length() - 1); // remove last comma
        json.append("}");

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        SecretKeySpec key = deriveKey(masterPassword, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        byte[] encrypted = cipher.doFinal(json.toString().getBytes(StandardCharsets.UTF_8));

        String content = Base64.getEncoder().encodeToString(salt) + ":" +
                         Base64.getEncoder().encodeToString(iv) + ":" +
                         Base64.getEncoder().encodeToString(encrypted);
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
    * Loads and decrypts the vault from an encrypted file on disk.
    *
    * @param filePath       Path to encrypted vault file
    * @param masterPassword Master password for decryption
    * @throws Exception     If decryption or file read fails
    */
    public void loadVault(String filePath, char[] masterPassword) throws Exception {
        String[] parts = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8).split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encrypted = Base64.getDecoder().decode(parts[2]);

        SecretKeySpec key = deriveKey(masterPassword, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
        String json = new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);

        // For now, skip parsing back to VaultEntry objects
        System.out.println("🔓 Decrypted Vault: " + json);
    }

    private SecretKeySpec deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
