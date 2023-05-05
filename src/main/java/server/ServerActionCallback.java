package server;

import server.data.ServerResponse;

/**
 * L'Interface {@code ServerActionCallback} doit être implémentée par n'importe quel objet dont la
 * volonté est de traiter une requête
 */
@FunctionalInterface
public interface ServerActionCallback {

    /**
     * Le traitement de la requête
     *
     * @return l'objet {@code Serializable} à renvoyer au client
     */
    public ServerResponse execute();
}
