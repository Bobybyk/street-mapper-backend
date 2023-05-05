package server.commands;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import server.Server;

public class ServerCommandUpdateTimeFile implements ServerCommand {
    
    private static final String DESCRIPTION = "change les informations des horaires du plan";

    private static final String FILE_ERROR = "Le ficher est un dossier ou inexistant";

    private static final String FILE_NOT_GIVEN = "s'attend à recevoir uniquement le chemin vers le nouveau fichier";

    @Override
    public String getdescription() {
        return DESCRIPTION;
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
            throw new IllegalArgumentException(FILE_NOT_GIVEN);

        String filePath = args[1];
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory())
            throw new IllegalArgumentException(FILE_ERROR);

        server.updateTime(file.getPath());
    }
    
}