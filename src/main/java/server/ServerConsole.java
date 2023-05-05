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

/**
 * Classe représentant la console du serveur
 */
class ServerConsole implements Runnable {

    /**
     * Le séparateur des arguments lus
     */
    static final String argsSplitter = " ";


    static final String PROMPT = "server >>> ";

    /**
     * Nom de la commande associée à {@code ServerCommandUpdateMapFile}
     */
    static final String UPDATE_MAP_NAME = "update-map";

    /**
     * Nom de la commande associée à {@code ServerCommandUpdateMapFile}
     */
    static final String UPDATE_TIME_NAME = "update-time";

    /**
     * Nom de la commande associée à {@code ServerCommandKill}
     */
    static final String KILL_NAME = "kill";

    /**
     * Nom de la commande associée à {@code ServerCommandDebug}
     */
    static final String DEBUG_NAME = "debug";

    static final String COMMAND_BORDER = "\n////////////////////////////////////////////////////////////\n";

    /**
     * Nom de la console
     */
    static final String SERVER_COMMAND_NAME = "TrainGo server terminal";

    /**
     * Map des commandes reconnues par le serveur
     */
    public static final Map<String, ServerCommand> commands = 
        Map.of(
            DEBUG_NAME, new ServerCommandDebug(),
            KILL_NAME, new ServerCommandKill(),
            UPDATE_MAP_NAME, new ServerCommandUpdateMapFile(),
            UPDATE_TIME_NAME, new ServerCommandUpdateTimeFile()
        );

    /**
     * Indique si la console est active
     */
    private boolean isRunning;


    private Scanner scanner;

    /**
     * Instance du serveur associée à la console
     */
    private Server server;

    ServerConsole(Server server) {
        isRunning = false;
        scanner = new Scanner(System.in);
        this.server = server;
    }

    /**
     * Affiche la réprésentation de {@code o} dans la sortie standard et flush cette sortie
     * 
     * @param o 
     */
    private static void write(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    /**
     * Affiche la réprésentation de {@code o} dans la sortie standard, retourne à la ligne et flush cette sortie
     * 
     * @param o 
     */
    private static void writeln(Object o) {
        System.out.println(o);
        System.out.flush();
    }

    /**
     * Sépare {@code line} par {@code argsSplitter} et appelle la commande coorespondant
     * @param line la ligne lue
     */
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

    /**
     * Affiche la liste des commandes de la console
     */
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

    /**
     * Affiche {@code PROMPT}
     */
    private void displayPrompt() {
        write(PROMPT);
    }

    /**
     * Arrête la console
     */
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


