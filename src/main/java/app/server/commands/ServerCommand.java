package app.server.commands;

import java.util.List;
import app.server.Server;

public interface ServerCommand {

    /**
     * @return description de la commande
     */
    String getdescription();

    /**
     * 
     * @param commandName nom de la commande associé a l'objet {@code ServerCommand}
     * @return une liste de descriptions visuelles sur l'usage de la commande 
     */
    List<String> getExemples(String commandName);

    /**
     * 
     * @param server instance du server en cours d'execution
     * @param args   arguments recues depuis d'entrée
     * @throws IllegalArgumentException si les arguements sont incompatibles avec la commande
     * @throws Exception n'importe quelle autre exception lancée par l'éxécution
     */
    void execute(Server server, String... args) throws IllegalArgumentException, Exception;
}