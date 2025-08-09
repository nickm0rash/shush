package shush.commands;

/**
 * Searches the vault for entries that match a given query string.
 * <p>
 * Placeholder: actual search will be implemented against the index/persistence layer.
 */
public class SearchCommand implements Command {

    /**
     * Executes the {@code shush search <query>} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Searching entries is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush search}. */
    private void printHelp() {
        System.out.println("Usage: shush search <query>");
        System.out.println("Searches for entries in the vault matching the query.");
    }
}
