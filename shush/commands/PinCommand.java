package shush.commands;

public class PinCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 PIN management is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush pin <options>");
        System.out.println("Manages the master PIN for accessing the vault.");
        System.out.println("Options can include setting, changing, or removing the PIN.");
    }
    
}
