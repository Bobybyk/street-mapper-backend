package app.server;

import java.io.Serializable;

/**
 * L'Interface {@code ServerActionCallback} doit être implémenté par n'importe quel objet dont la
 * volonté est de traiter une requête
 */
@FunctionalInterface
public interface ServerActionCallback {

    /**
     * Le traitement de la requête
     *
     * @return l'objet {@code Serializable} à renvoyer au client
     */
    public Serializable execute();
}
