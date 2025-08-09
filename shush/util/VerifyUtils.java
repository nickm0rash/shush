package shush.util;

import shush.util.TOTPManager;
import shush.vault.VaultManager;

import java.io.Console;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Centralized verification helper for Shush commands.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Prompt for and verify the master password via {@link VaultManager}.</li>
 *   <li>Optionally prompt for and verify a TOTP code via {@link TOTPManager}.</li>
 *   <li>Provide the caller with an ephemeral {@code char[]} master password to use for crypto operations.</li>
 * </ul>
 *
 * <h2>Security notes</h2>
 * <ul>
 *   <li>This class never persists secrets. The returned {@link VerificationResult} holds secrets in memory only.</li>
 *   <li>Callers <b>must</b> invoke {@link VerificationResult#close()} or {@link VerificationResult#zeroSecrets()} to wipe memory ASAP.</li>
 *   <li>Where possible, {@code char[]} is used instead of {@code String} to minimize immutable secret lifetimes.</li>
 * </ul>
 */
public final class VerifyUtils {

    private VerifyUtils() { /* no instances */ }

    /**
     * Performs the standard verification flow used by commands like {@code shush add}:
     * <ol>
     *   <li>Prompt for master password and verify against {@link VaultManager}.</li>
     *   <li>If {@code requires2FA} is true, prompt for a TOTP code and verify.</li>
     * </ol>
     *
     * @param vaultManager active vault manager instance
     * @param requires2FA  whether this action requires TOTP verification
     * @return a {@link VerificationResult} containing success state and (on success) the master password chars
     */
    public static VerificationResult promptAndVerify(VaultManager vaultManager, boolean requires2FA) {
        Objects.requireNonNull(vaultManager, "vaultManager");

        Console console = System.console();
        char[] master = null;

        try (Scanner scanner = (console == null ? new Scanner(System.in) : null)) {
            // --- Prompt master password ---
            if (console != null) {
                master = console.readPassword("Master password: ");
            } else {
                System.out.print("Master password: ");
                master = scanner.nextLine().toCharArray();
            }

            if (master == null || master.length == 0) {
                return VerificationResult.failure("Empty master password.");
            }

            if (!vaultManager.verifyMasterPassword(master)) {
                return VerificationResult.failureAndZero(master, "Invalid master password.");
            }

            // --- Optional TOTP ---
            if (requires2FA) {
                final String totpSecret = vaultManager.getTOTPSecret();
                if (totpSecret == null || totpSecret.isEmpty()) {
                    return VerificationResult.failureAndZero(master, "TOTP required, but no TOTP secret is configured.");
                }

                final String code;
                if (console != null) {
                    char[] codeChars = console.readPassword("TOTP code: ");
                    code = (codeChars == null) ? "" : new String(codeChars);
                    zeroChars(codeChars);
                } else {
                    System.out.print("TOTP code: ");
                    code = scanner.nextLine();
                }

                if (!TOTPManager.verifyCode(totpSecret, code)) {
                    return VerificationResult.failureAndZero(master, "Invalid TOTP code.");
                }
            }

            // Success: return master password to caller (they must zero it when finished)
            return VerificationResult.success(master);

        } catch (GeneralSecurityException gse) {
            return VerificationResult.failureAndZero(master, "Verification failed: " + gse.getMessage());
        } catch (RuntimeException re) {
            return VerificationResult.failureAndZero(master, "Verification error: " + re.getMessage());
        }
    }

    /** Best-effort zeroing of sensitive char arrays. */
    private static void zeroChars(char[] arr) {
        if (arr != null) for (int i = 0; i < arr.length; i++) arr[i] = 0;
    }

    /**
     * Result container for verification steps.
     * <p>Implements {@link AutoCloseable} so callers can use try-with-resources to ensure zeroing.</p>
     */
    public static final class VerificationResult implements AutoCloseable {
        private final boolean success;
        private final String failureReason;
        private char[] masterPassword; // present only if success == true

        private VerificationResult(boolean success, char[] masterPassword, String failureReason) {
            this.success = success;
            this.masterPassword = masterPassword;
            this.failureReason = failureReason;
        }

        /** @return {@code true} if all required verifications passed. */
        public boolean isSuccess() { return success; }

        /** @return the master password as {@code char[]} if {@link #isSuccess()} is true; otherwise {@code null}. */
        public char[] getMasterPassword() { return masterPassword; }

        /** @return a human-readable reason when {@link #isSuccess()} is false; otherwise {@code null}. */
        public String getFailureReason() { return failureReason; }

        /** Wipes the master password from memory (best-effort). Safe to call multiple times. */
        public void zeroSecrets() { VerifyUtils.zeroChars(masterPassword); masterPassword = null; }

        /** Equivalent to {@link #zeroSecrets()}. */
        @Override public void close() { zeroSecrets(); }

        // ---- factories ----
        static VerificationResult success(char[] master) {
            return new VerificationResult(true, master, null);
        }
        static VerificationResult failure(String reason) {
            return new VerificationResult(false, null, reason);
        }
        static VerificationResult failureAndZero(char[] master, String reason) {
            VerifyUtils.zeroChars(master);
            return new VerificationResult(false, null, reason);
        }
    }
}
