package shush.commands;


public class TotpAddCommand implements Command{
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Adding TOTP entry is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush totp add <entry-name>");
        System.out.println("Adds a new TOTP entry to the vault.");
    }
}
