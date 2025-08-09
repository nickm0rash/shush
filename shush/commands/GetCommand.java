package shush.commands;

/**
 * Retrieves a single entry from the vault and displays it to the user.
 * <p>
 * This is a placeholder that will later fetch from the persistence layer.
 */
public class GetCommand implements Command {

    /**
     * Executes the {@code shush get <entry-name>} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Retrieving entry is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush get}. */
    private void printHelp() {
        System.out.println("Usage: shush get <entry-name>");
        System.out.println("Retrieves and displays the specified entry from the vault.");
    }
}
