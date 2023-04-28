package app.server;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;


interface ServerCommand {
    void execute(String... args) throws IllegalArgumentException;
}

class SCUpdateMapFile implements ServerCommand {
    @Override
    public void execute(String... args) {
        if (args.length != 2) 
        throw new IllegalArgumentException("UpdateMap s'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];
    }
}

class ServerConsole implements Runnable {

    public static final String argsSplitter = " ";
    public static final String PROMPT = "server >>> ";

    public static final Map<String, ServerCommand> commands = 
        Map.of(
            "update-map", new SCUpdateMapFile()
        );

    private boolean isRunning;
    private Scanner scanner;

    public ServerConsole() {
        isRunning = false;
        scanner = new Scanner(System.in);
        // this.server = server;
    }

    public void dispatchFromInput(String line) {
        String[] args = line.split(argsSplitter);
        String commandString = args[0];
        ServerCommand command = commands.get(commandString);
        if (command == null) {
            write("Unknwon command : " + command + "\n");
        } else {
            command.execute(args);
        }
    }

    private void write(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    public void displayPrompt() {
        write(PROMPT);
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                displayPrompt();
                String line = scanner.nextLine();
                dispatchFromInput(line);
            } catch (IllegalStateException | NoSuchElementException e) {
               isRunning = false;
            }
        }
        scanner.close();
    }
}


