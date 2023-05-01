package app.server;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import app.map.Plan;
import app.map.PlanParser;


interface ServerCommand {

    String getdescription();
    void execute(Server server, String... args) throws IllegalArgumentException, Exception;
}

class SCUpdateMapFile implements ServerCommand {

    @Override
    public String getdescription() {
        return "commande permettant de changer le ficher de plan";
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) 
            throw new IllegalArgumentException("UpdateMap s'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];
        Plan plan = PlanParser.planFromSectionCSV(filePath);
        server.updateMap(plan);
    }
}

class SCUpdateTimeFile implements ServerCommand {

    @Override
    public String getdescription() {
        return "commande permettant de changer les informations de temps au plan";
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) 
            throw new IllegalArgumentException("S'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];

        server.updateTime(filePath);
    }
    
}

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

    private void dispatchFromInput(String line) {
        String[] args = line.split(argsSplitter);
        String commandString = args[0];
        ServerCommand command = commands.get(commandString);
        if (command == null) {
            writeln("Unknwon command : " + command);
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
            .forEach(this::writeln);
    }

    private void write(Object o) {
        System.out.print(o);
        System.out.flush();
    }

    private void writeln(Object o) {
        System.out.println(o);
        System.out.flush();
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


