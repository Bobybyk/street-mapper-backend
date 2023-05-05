package server.commands;

import java.util.ArrayList;
import java.util.List;

import server.Server;

public class ServerCommandKill implements ServerCommand {

    private final String description = "arrête le serveur";
    @Override
    public String getdescription() {
        return description;
    }

    @Override
    public List<String> getExemples(String commandName) {
        return new ArrayList<>();
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 1) throw new IllegalArgumentException("La commande ne s'attend à aucun argument");

        server.stop();
    }
    
}
