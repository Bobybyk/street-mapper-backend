package server;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import server.commands.ServerCommand;
import server.commands.ServerCommandDebug;
import server.commands.ServerCommandKill;
import server.commands.ServerCommandUpdateMapFile;
import server.commands.ServerCommandUpdateTimeFile;

class ServerConsole implements Runnable {

    static final String argsSplitter = " ";
    static final String PROMPT = "server >>> ";

    static final String UPDATE_MAP_NAME = "update-map";
    static final String UPDATE_TIME_NAME = "update-time";
    static final String KILL_NAME = "kill";
    static final String DEBUG_NAME = "debug";

    static final String COMMAND_BORDER = "\n////////////////////////////////////////////////////////////\n";
    static final String SERVER_COMMAND_NAME = "TrainGo server terminal";

    public static final Map<String, ServerCommand> commands = 
        Map.of(
            DEBUG_NAME, new ServerCommandDebug(),
            KILL_NAME, new ServerCommandKill(),
            UPDATE_MAP_NAME, new ServerCommandUpdateMapFile(),
            UPDATE_TIME_NAME, new ServerCommandUpdateTimeFile()
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

    void dispatchFromInput(String line) {
        String[] args = line.split(argsSplitter);

        if (args.length == 0) {
            writeln(new StringBuffer("Commande vide").toString());
            return;
        }

        String commandString = args[0];
        ServerCommand command = commands.get(commandString);
        if (command == null) {
            writeln(new StringBuffer("Commande non reconnu : ").append(commandString).toString());
            return;
        }
        
        try {
            command.execute(server, args);
        } catch (Exception e) {
            writeln(new StringBuffer("Error : ").append(e.getMessage()).toString());
        } 

    }

    private void displayCommandHeader() {
        int blankBeforeName = COMMAND_BORDER.length() - SERVER_COMMAND_NAME.length();
        blankBeforeName = Integer.max(blankBeforeName, 0) / 2;
        writeln(COMMAND_BORDER);
        IntStream.range(0, blankBeforeName).forEach(ignore -> write(" "));;
        writeln(SERVER_COMMAND_NAME);
        writeln("");
    }

    private void displayCommand() {
        displayCommandHeader();
        String splitter = "\n      ";
        commands.entrySet()
            .stream()
            .map( entry -> 
                String.format("  %s : %s%s%s\n", 
                entry.getKey(), 
                entry.getValue().getdescription(),
                splitter,
                entry.getValue().getExemples(entry.getKey()).stream().collect(Collectors.joining(splitter))
                )
            ).forEach(ServerConsole::writeln);
        writeln(COMMAND_BORDER);
    }

    private void displayPrompt() {
        write(PROMPT);
    }

    public synchronized void stop() {
        isRunning = false;
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


