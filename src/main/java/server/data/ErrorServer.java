package server.data;

import java.io.Serial;
import java.io.Serializable;



/**
 * Classe ErrorServer renvoie une erreur au client si: -les entrées de l'utilisateur sont mauvaises
 * -si les champs ne sont pas respectés (manquant)
 */
public class ErrorServer implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private final String error;

    public ErrorServer(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
