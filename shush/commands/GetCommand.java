package shush.commands;

public class GetCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Retrieving entry is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush get <entry-name>");
        System.out.println("Retrieves and displays the specified entry from the vault.");
    }   
    
}
