package server.commands;

import java.util.Arrays;
import java.util.List;

import server.Server;

public class ServerCommandUpdateMapFile implements ServerCommand {

    @Override
    public String getdescription() {
        return "commande permettant de changer le ficher de plan";
    }

    @Override
    public List<String> getExemples(String commandName) {
        return Arrays.asList(
            new StringBuilder().append(commandName).append(" <ficher des stations>").toString()
        );
    }

    @Override
    public void execute(Server server, String... args) throws IllegalArgumentException, Exception {
        if (args.length != 2) 
            throw new IllegalArgumentException("s'attend à recevoir uniquement le chemin vers le nouveau fichier");
        String filePath = args[1];
        server.updateMap(filePath);
    }
}