package shush.commands;

/**
 * Removes an existing entry from the vault.
 * <p>
 * Placeholder: actual removal will be implemented using the persistence layer.
 */
public class RemoveCommand implements Command {

    /**
     * Executes the {@code shush remove <entry-name>} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Entry removal is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush remove}. */
    private void printHelp() {
        System.out.println("Usage: shush remove <entry-name>");
        System.out.println("Removes the specified entry from the vault.");
    }
}
