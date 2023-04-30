package app.server;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import app.map.Plan;
import app.map.PlanParser;
import app.map.PlanParser.IncorrectFileFormatException;


interface ServerCommand {
    void execute(Server server, String... args) throws IllegalArgumentException, FileNotFoundException;
}

class SCUpdateMapFile implements ServerCommand {
    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, FileNotFoundException {
        if (args.length != 2) 
            throw new IllegalArgumentException("UpdateMap s'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];
        try {
        
            Plan plan = PlanParser.planFromSectionCSV(filePath);
            server.updateMap(plan);
        } catch ( IncorrectFileFormatException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (InterruptedException e ) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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
    private Server server;

    public ServerConsole(Server server) {
        isRunning = false;
        scanner = new Scanner(System.in);
        this.server = server;
    }

    public void dispatchFromInput(String line) {
        String[] args = line.split(argsSplitter);
        String commandString = args[0];
        ServerCommand command = commands.get(commandString);
        if (command == null) {
            write("Unknwon command : " + command + "\n");
        } else {
            try {
                command.execute(server, args);
            } catch (IllegalArgumentException | FileNotFoundException e) {
                write(e.getMessage().concat("\n"));
            }
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


