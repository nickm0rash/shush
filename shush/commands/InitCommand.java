package shush.commands;

import java.util.Scanner;

/**
 * Initializes a new Shush vault by interactively collecting setup options.
 * <p>
 * Prompts for:
 * <ul>
 *   <li>Vault name (defaults to {@code "default"})</li>
 *   <li>Whether to enable TOTP 2FA</li>
 *   <li>If TOTP is enabled, whether to require it by default for all entries</li>
 *   <li>If TOTP is not enabled, an optional master PIN</li>
 * </ul>
 * <p>
 * Note: This command currently prints placeholders and does not yet call the
 * persistence layer. Wire this to {@code VaultManager.createVault(...)} in a future pass.
 */
public class InitCommand implements Command {

    /**
     * Executes the {@code shush init} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Welcome to Shush Vault Setup");

        Scanner scanner = new Scanner(System.in);

        // Vault name
        System.out.print("Vault name (default: default): ");
        String vaultName = scanner.nextLine().trim();
        if (vaultName.isEmpty()) {
            vaultName = "default";
        }

        // Enable TOTP?
        System.out.print("Enable TOTP 2FA? (Y/n): ");
        String totpResponse = scanner.nextLine().trim().toLowerCase();
        boolean enableTOTP = !"n".equals(totpResponse);

        boolean totpDefault = false;
        if (enableTOTP) {
            // Ask if TOTP should be used by default for all entries
            System.out.print("Use TOTP by default for all entries? (Y/n): ");
            String totpDefaultResp = scanner.nextLine().trim().toLowerCase();
            totpDefault = !"n".equals(totpDefaultResp);
        }

        // If not using TOTP, ask to set a PIN
        String pin = "";
        if (!enableTOTP) {
            System.out.print("Set a master PIN (optional, leave blank to skip): ");
            pin = scanner.nextLine().trim();
        }

        // Placeholder output
        System.out.println("\nCreating vault '" + vaultName + "'...");
        if (!enableTOTP) {
            System.out.println("PIN: " + (pin.isEmpty() ? "(none)" : "••••"));
        } else {
            System.out.println("TOTP enabled: true");
            System.out.println("TOTP default: " + totpDefault);
        }

        // TODO: call VaultManager.createVault(vaultName, pin, enableTOTP, totpDefault);
        System.out.println("✅ Vault initialized successfully!\n");
    }

    /** Prints concise usage for {@code shush init}. */
    private void printHelp() {
        System.out.println("Usage: shush init");
        System.out.println("Initializes a new secure vault.");
        System.out.println("You'll be asked to enable two-factor authentication (TOTP) or set a PIN.");
    }
}
