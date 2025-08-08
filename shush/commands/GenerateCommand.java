package shush.commands;

public class GenerateCommand implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }

        System.out.println("🔐 Password generation is not yet implemented.");
        System.out.println("Please check back in a future release.");
    }

    private void printHelp() {
        System.out.println("Usage: shush gen [options]");
        System.out.println("Generates a strong password based on specified options.");
        System.out.println("Options can include length, use of symbols, numbers, etc.");
    }   
    
}
