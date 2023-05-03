package app.server.commands;

import java.util.Arrays;
import java.util.List;
import app.server.Server;

public class ServerCommandUpdateTimeFile implements ServerCommand {

    @Override
    public String getdescription() {
        return "commande permettant de changer les informations de temps au plan";
    }

    @Override
    public List<String> getExemples(String commandName) {
        return Arrays.asList(
            new StringBuilder().append(commandName).append(" <ficher des horaires>").toString()
        );
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) 
            throw new IllegalArgumentException("S'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];

        server.updateTime(filePath);
    }
    
}