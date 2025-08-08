package shush.commands;

public class AddCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Add command is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush add <options>");
        System.out.println("Adds a new entry to the vault.");
        System.out.println("Options can include name, username, password, URL, notes, and TOTP settings.");
    }
    
}
