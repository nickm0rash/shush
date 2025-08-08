package shush.commands;

import java.util.Scanner;

public class InitCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Welcome to Shush Vault Setup");

        Scanner scanner = new Scanner(System.in);

        // Vault name
        System.out.print("Vault name (default: default): ");
        String vaultName = scanner.nextLine().trim();
        if (vaultName.isEmpty()) vaultName = "default";

        // Enable TOTP?
        System.out.print("Enable TOTP 2FA? (Y/n): ");
        String totpResponse = scanner.nextLine().trim().toLowerCase();
        boolean enableTOTP = !totpResponse.equals("n");

        boolean totpDefault = false;
        if (enableTOTP) {
            // Ask if TOTP should be used by default for all entries
            System.out.print("Use TOTP by default for all entries? (Y/n): ");
            String totpDefaultResp = scanner.nextLine().trim().toLowerCase();
            totpDefault = !totpDefaultResp.equals("n");
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

    private void printHelp() {
        System.out.println("Usage: shush init");
        System.out.println("Initializes a new secure vault.");
        System.out.println("You’ll be asked to enable two-factor authentication (TOTP) or set a PIN.");
    }
}
