package app.server;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

class ServerConsole implements Runnable {

    static final String argsSplitter = " ";
    static final String PROMPT = "server >>> ";

    public final Map<String, ServerCommand> commands = 
        Map.of(
            "update-map", new SCUpdateMapFile(),
            "update-time", new SCUpdateTimeFile()
        );

    private boolean isRunning;
    private Scanner scanner;
    private Server server;

    ServerConsole(Server server) {
        isRunning = false;
        scanner = new Scanner(System.in);
        this.server = server;
    }

    private static void write(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    private static void writeln(Object o) {
        System.out.println(o);
        System.out.flush();
    }

    private void dispatchFromInput(String line) {
        String[] args = line.split(argsSplitter);
        String commandString = args[0];
        ServerCommand command = commands.get(commandString);
        if (command == null) {
            writeln("Unknwon command : " + commandString);
            return;
        }
        
        try {
            command.execute(server, args);
        } catch (Exception e) {
            writeln(e.getMessage());
        } 

    }

    private void displayCommand() {
        commands.entrySet()
            .stream()
            .map( entry -> String.format("%s : %s", entry.getKey(), entry.getValue().getdescription()))
            .forEach(ServerConsole::writeln);
    }

    private void displayPrompt() {
        write(PROMPT);
    }

    @Override
    public void run() {
        isRunning = true;
        displayCommand();
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


