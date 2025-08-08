package shush.commands;

public class ImportCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Importing entries from CSV is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush import-csv <file>");
        System.out.println("Imports entries from a CSV file into the vault.");
        System.out.println("The CSV file should have columns: name, username, password, url, notes.");
    }
    
}
