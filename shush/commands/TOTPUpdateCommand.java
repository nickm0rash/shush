package shush.commands;


public class TotpUpdateCommand implements Command{
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Updating TOTP entry is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush totp update <entry-name>");
        System.out.println("Updates the specified TOTP entry in the vault.");
    }
}
