package shush.commands;

public class UpdateCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Entry update is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush update <entry-name> <options>");
        System.out.println("Updates the specified entry in the vault with new information.");
        System.out.println("Options can include changing the username, password, URL, notes, or TOTP settings.");
    }
    
}
