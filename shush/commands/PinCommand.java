package shush.commands;

/**
 * Manages the vault's master PIN (set, change, remove) when TOTP is not the default.
 * <p>
 * Placeholder: actual PIN management will be integrated with the vault's secure storage.
 */
public class PinCommand implements Command {

    /**
     * Executes the {@code shush pin} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 PIN management is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush pin}. */
    private void printHelp() {
        System.out.println("Usage: shush pin <options>");
        System.out.println("Manages the master PIN for accessing the vault.");
        System.out.println("Options can include setting, changing, or removing the PIN.");
    }
}
