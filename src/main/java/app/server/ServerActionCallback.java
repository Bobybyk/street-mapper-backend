package app.server;

import java.io.IOException;
import java.io.Serializable;

/**
 * l'Interface {@code ServerActionCallback} doit etre implémenté par l'importe quel objet dont la volonté 
 * est de communiqué avec le client
 */
@FunctionalInterface
public interface ServerActionCallback {

    /**
     * 
     * @param s            Ligne (chaine de caractere) lue dans le sockets 
     * @param socket       Socket sur lequel la réponse sera envoyée
     * @throws IOException si une erreur arrive lors de la manipulation des entrées/sorties du socket
     */
    public Serializable execute(String s) throws IOException;
}