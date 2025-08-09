package shush.commands;

/**
 * Imports entries into the current vault from a CSV file.
 * <p>
 * Expected CSV columns: {@code name, username, password, url, notes}.
 * This is a placeholder; actual import logic will be implemented later.
 */
public class ImportCommand implements Command {

    /**
     * Executes the {@code shush import-csv <file>} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Importing entries from CSV is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush import-csv}. */
    private void printHelp() {
        System.out.println("Usage: shush import-csv <file>");
        System.out.println("Imports entries from a CSV file into the vault.");
        System.out.println("The CSV file should have columns: name, username, password, url, notes.");
    }
}
