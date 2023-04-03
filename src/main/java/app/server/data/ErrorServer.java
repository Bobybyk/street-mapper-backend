package app.server.data;

import java.io.Serial;
import java.io.Serializable;

public class ErrorServer implements Serializable {

    /**
     * Class ErrorServer renvoie une erreur au serveur si:
     * -les entrées de l'utilisateur sont mauvaises
     * -si les champs ne sont pas respectés (manquant)
     */

    @Serial
    private static final long serialVersionUID = 2L;

    private final String error;

    public ErrorServer(String error){
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
