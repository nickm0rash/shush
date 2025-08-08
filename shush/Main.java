package shush;

import shush.commands.*;

import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Main {
    private static final Map<String, Command> commands = new HashMap<>();
    

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String commandName = args[0].toLowerCase();

        // Lazy initialize commands
        registerCommands();

        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Unknown command: " + commandName);
            printHelp();
            return;
        }

        // Slice off the command name and pass the rest
        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

        try {
            command.execute(commandArgs);
        } catch (Exception e) {
            System.out.println("Error running command: " + e.getMessage());
            e.printStackTrace(); // Optional: suppress in production
        }
    }

    private static void registerCommands() {
        commands.put("init", new InitCommand());
        commands.put("add", new AddCommand());
        commands.put("get", new GetCommand());
        commands.put("gen", new GenerateCommand());
        commands.put("search", new SearchCommand());
        commands.put("rm", new RemoveCommand());
        commands.put("update", new UpdateCommand());
        commands.put("pin", new PinCommand());
        commands.put("connect", new ConnectCommand());
        commands.put("import-csv", new ImportCommand());

        // TOTP-related
        commands.put("totp-add", new TotpAddCommand());
        commands.put("totp-remove", new TotpRemoveCommand());
        commands.put("totp-update", new TotpUpdateCommand());

        //TODO: Other 2FA, Twilio, etc.
    }

    private static void printHelp() {
    try (InputStream input = Main.class.getResourceAsStream("/resources/help-root.txt")) {
        if (input == null) {
            System.out.println("Help file not found.");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    } catch (IOException e) {
        System.out.println("Error reading help file: " + e.getMessage());
    }
}

}
