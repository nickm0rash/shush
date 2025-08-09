package shush.vault;

import shush.util.CryptoUtils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * High-level manager for vault operations, configuration, and persistence.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Holds global configuration (e.g., "Require TOTP for all entries").</li>
 *   <li>Verifies master password (against a persisted PBKDF2 hash).</li>
 *   <li>Exposes TOTP secret for verification (secret itself should be stored securely).</li>
 *   <li>Adds/updates/removes entries and persists changes.</li>
 * </ul>
 *
 * <h2>Notes</h2>
 * <ul>
 *   <li>This reference implementation keeps entries in-memory and omits real file I/O;
 *       integrate your chosen storage later (JSON/DB) inside {@link #persist()} and {@link #load()}.</li>
 *   <li>Master password verification uses PBKDF2-HMAC-SHA256 with a stored salt+hash.</li>
 * </ul>
 */
public class VaultManager {

    // ---- configuration & state (backed by persistence in your final build) ----
    private volatile boolean globalTOTPEnabled;
    private volatile String totpSecret; // store securely; rotate via your init/config flows

    // Master password verifier (salted PBKDF2 hash)
    private final byte[] masterSalt;
    private final byte[] masterHash;
    private final int masterHashIterations;
    private final int masterHashBits;

    // Entries (encrypted strings, as produced by CryptoUtils)
    private final List<VaultEntry> entries = new ArrayList<>();

    // Concurrency
    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock(true);

    /**
     * Constructs a {@code VaultManager}.
     *
     * @param globalTOTPEnabled whether all entries require TOTP by default
     * @param totpSecret        the configured TOTP secret (Base32 or whatever encoding your TOTPManager expects)
     * @param masterSalt        salt used for master password PBKDF2 verification
     * @param masterHash        stored PBKDF2 hash of the master password
     * @param iterations        PBKDF2 iterations (e.g., 210_000)
     * @param hashBits          hash length in bits (e.g., 256)
     */
    public VaultManager(boolean globalTOTPEnabled,
                        String totpSecret,
                        byte[] masterSalt,
                        byte[] masterHash,
                        int iterations,
                        int hashBits) {
        this.globalTOTPEnabled = globalTOTPEnabled;
        this.totpSecret = totpSecret;
        this.masterSalt = Objects.requireNonNull(masterSalt, "masterSalt");
        this.masterHash = Objects.requireNonNull(masterHash, "masterHash");
        this.masterHashIterations = iterations;
        this.masterHashBits = hashBits;
    }

    // ----------------- config getters -----------------

    /** @return true if "Require TOTP for all entries" is enabled. */
    public boolean isGlobalTOTPEnabled() {
        return globalTOTPEnabled;
    }

    /** Allows toggling global TOTP (should be gated by your config command). */
    public void setGlobalTOTPEnabled(boolean enabled) {
        this.globalTOTPEnabled = enabled;
        persistQuietly();
    }

    /** @return the configured TOTP secret (managed elsewhere; this manager only exposes it for verification). */
    public String getTOTPSecret() {
        return totpSecret;
    }

    /** Updates the TOTP secret (use from your init/config flows). */
    public void setTOTPSecret(String secret) {
        this.totpSecret = secret;
        persistQuietly();
    }

    // ----------------- master password verification -----------------

    /**
     * Verifies the supplied master password against the stored PBKDF2-HMAC-SHA256 hash.
     *
     * @param masterPassword master password as {@code char[]} (will not be persisted)
     * @return true if the password matches
     * @throws GeneralSecurityException if the KDF fails
     */
    public boolean verifyMasterPassword(char[] masterPassword) throws GeneralSecurityException {
        Objects.requireNonNull(masterPassword, "masterPassword");
        byte[] derived = null;
        try {
            PBEKeySpec spec = new PBEKeySpec(masterPassword, masterSalt, masterHashIterations, masterHashBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            derived = skf.generateSecret(spec).getEncoded();
            return constantTimeEquals(derived, masterHash);
        } finally {
            zero(derived);
            zeroChars(masterPassword);
        }
    }

    /**
     * Convenience overload for legacy call-sites (avoid using this if possible).
     */
    public boolean verifyMasterPassword(String masterPassword) throws GeneralSecurityException {
        return verifyMasterPassword(masterPassword == null ? new char[0] : masterPassword.toCharArray());
    }

    // ----------------- entries -----------------

    /**
     * Adds an entry to the vault and persists the change.
     *
     * @param entry a {@link VaultEntry} with encrypted fields
     */
    public void addEntry(VaultEntry entry) {
        Objects.requireNonNull(entry, "entry");
        rw.writeLock().lock();
        try {
            // Optional: enforce unique labels here if that's a rule.
            entries.add(entry);
            persist();
        } finally {
            rw.writeLock().unlock();
        }
    }

    /** Returns an immutable snapshot of entries (still encrypted). */
    public List<VaultEntry> listEntries() {
        rw.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(entries));
        } finally {
            rw.readLock().unlock();
        }
    }

    // ----------------- persistence hooks (stubbed) -----------------

    /** Loads vault state from disk (stub). Call during app startup. */
    public void load() {
        // TODO: implement disk I/O (read encrypted file, parse JSON/CBOR, hydrate entries and config).
    }

    /** Persists vault state to disk (stub). */
    public void persist() {
        // TODO: implement disk I/O (write encrypted file).
        // TIP: Your outer layer can encrypt a serialized structure with CryptoUtils before writing to disk.
    }

    private void persistQuietly() {
        try {
            persist();
        } catch (RuntimeException ex) {
            // optional: log
        }
    }

    // ----------------- utils -----------------

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= (a[i] ^ b[i]);
        return r == 0;
    }

    private static void zero(byte[] arr) {
        if (arr != null) for (int i = 0; i < arr.length; i++) arr[i] = 0;
    }

    private static void zeroChars(char[] arr) {
        if (arr != null) for (int i = 0; i < arr.length; i++) arr[i] = 0;
    }
}
