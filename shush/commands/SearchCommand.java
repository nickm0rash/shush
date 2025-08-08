package shush.commands;

public class SearchCommand implements Command {
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Searching entries is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush search <query>");
        System.out.println("Searches for entries in the vault matching the query.");
    }
    
}
