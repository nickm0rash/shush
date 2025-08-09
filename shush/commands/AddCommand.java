package shush.commands;

import java.nio.charset.StandardCharsets;
import shush.util.CryptoUtils;
import shush.util.VerifyUtils;
import shush.vault.VaultEntry;
import shush.vault.VaultManager;

/**
 * Handles the {@code shush add} command, which creates and stores a new vault entry.
 * <p>
 * Flow:
 * <ol>
 *   <li>Decide if this entry requires 2FA (global TOTP vs flags).</li>
 *   <li>Use {@link VerifyUtils} to prompt and verify master password and (optionally) TOTP.</li>
 *   <li>Encrypt username/password/comment with AES-GCM using a key derived from the master password.</li>
 *   <li>Persist the entry via {@link VaultManager}.</li>
 * </ol>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * shush add github -u myuser -p mypass -2FA -c "Personal GitHub account"
 * }</pre>
 */
public class AddCommand implements Command {

    /** Manager for vault operations and configuration. */
    private final VaultManager vaultManager;

    /**
     * Constructs a new {@code AddCommand}.
     *
     * @param vaultManager the active {@link VaultManager} used for config checks and storage
     */
    public AddCommand(VaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }

    /**
     * Executes the {@code shush add} command using raw CLI args.
     * <p>
     * Placeholder: argument parsing not yet implemented.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        // TODO: Parse args and call the overload below with appropriate parameters.
        System.out.println("AddCommand: execute(String[] args) not yet implemented.");
    }

    /**
     * Adds an entry with the provided field values.
     *
     * @param label    the label (unique identifier) for the entry
     * @param username the username (may be {@code null})
     * @param password the password (may be {@code null})
     * @param comment  optional comment/description (may be {@code null})
     * @param force2FA {@code true} if the {@code -2FA} flag was passed to require TOTP for this entry
     * @param no2FA    {@code true} if the {@code -no2fa} flag was passed to disable TOTP for this entry
     */
    public void execute(String label, String username, String password, String comment,
                        boolean force2FA, boolean no2FA) {
        // 1) Determine if this entry requires TOTP
        final boolean globalTOTP = vaultManager.isGlobalTOTPEnabled();
        final boolean requires2FA = globalTOTP ? !no2FA : force2FA;

        // 2) Centralized verification (master pw + optional TOTP)
        try (VerifyUtils.VerificationResult vr = VerifyUtils.promptAndVerify(vaultManager, requires2FA)) {
            if (!vr.isSuccess()) {
                System.out.println("Error: " + vr.getFailureReason());
                return;
            }

            // 3) Encrypt sensitive fields using the ephemeral master password (char[])
            char[] master = vr.getMasterPassword();

            final String encUser = CryptoUtils.encrypt(safeBytes(username), master);
            final String encPass = CryptoUtils.encrypt(safeBytes(password), master);
            final String encComment = CryptoUtils.encrypt(safeBytes(comment), master);

            // 4) Persist
            VaultEntry entry = new VaultEntry(label, encUser, encPass, encComment, requires2FA);
            vaultManager.addEntry(entry);

            System.out.println("Entry '" + label + "' added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding entry: " + e.getMessage());
        }
    }

    // ---- helpers ----

    /**
     * Converts a possibly-null string to UTF-8 bytes.
     *
     * @param s the string, or {@code null}
     * @return UTF-8 bytes; empty array if {@code s} is {@code null}
     */
    private static byte[] safeBytes(String s) {
        return s == null ? new byte[0] : s.getBytes(StandardCharsets.UTF_8);
    }
}
