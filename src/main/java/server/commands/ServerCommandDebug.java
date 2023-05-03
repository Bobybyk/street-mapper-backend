package server.commands;

import java.util.Arrays;
import java.util.List;

import server.Server;
import util.Logger;

public class ServerCommandDebug implements ServerCommand {

    private static final String USAGE_EXAMPLE = " <0 | 1>";

    private final String description = "gere l'activation de l'affichage des logs";

    @Override
    public String getdescription() {
        return description;
    }

    @Override
    public List<String> getExemples(String commandName) {
        return Arrays.asList(
            new StringBuilder(commandName).append( " 0").toString(),
                new StringBuilder(commandName).append( " 1").toString()
        );
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) {
            String message = new StringBuilder("mauvais format. Attend : ")
                .append(args.length > 1 ? args[0] : "")
                .append(" ")
                .append(USAGE_EXAMPLE)
                .toString();
            throw new IllegalArgumentException(message);
        }

        switch (args[1].trim()) {
            case "0" -> {
                Logger.info("logger désactivé");
                Logger.disable();
            }
            case "1" -> {
                Logger.enable();
                Logger.info("logger activé");
            }
            default -> Logger.error("Argument non reconnu");
        }
    }
    
}
