package shush.commands;

/**
 * Generates a strong password according to the provided options.
 * <p>
 * Placeholder: actual generation logic will be wired to a secure RNG and options
 * such as length, character classes, and presets.
 */
public class GenerateCommand implements Command {

    /**
     * Executes the {@code shush gen} command.
     *
     * @param args CLI arguments; supports {@code --help} or {@code -h} to show usage
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 0 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp();
            return;
        }

        System.out.println("🔐 Password generation is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    /** Prints concise usage for {@code shush gen}. */
    private void printHelp() {
        System.out.println("Usage: shush gen [options]");
        System.out.println("Generates a strong password based on specified options.");
        System.out.println("Options can include length, use of symbols, numbers, etc.");
    }
}
