package shush.commands;

/**
 * Updates fields on an existing vault entry (e.g., username, password, URL, notes, TOTP settings).
 * <p>
 * Placeholder: actual update logic will be implemented later.
 */
public class UpdateCommand implements Command {

    /**
     * Executes the {@code shush update <entry-name> <options>} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Entry update is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush update}. */
    private void printHelp() {
        System.out.println("Usage: shush update <entry-name> <options>");
        System.out.println("Updates the specified entry in the vault with new information.");
        System.out.println("Options can include changing the username, password, URL, notes, or TOTP settings.");
    }
}
