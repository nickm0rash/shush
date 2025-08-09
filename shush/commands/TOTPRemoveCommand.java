package shush.commands;


public class TOTPRemoveCommand implements Command {
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Removing TOTP entry is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush totp remove <entry-name>");
        System.out.println("Removes the specified TOTP entry from the vault.");
    }
}
