package app.server.commands;

import java.util.ArrayList;
import java.util.List;
import app.server.Server;

public class ServerCommandKill implements ServerCommand {

    @Override
    public String getdescription() {
        return "arrête le serveur";
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
