package server.data;

import java.io.Serial;



/**
 * Classe ErrorServer renvoie une erreur au client si: -les entrées de l'utilisateur sont mauvaises
 * -si les champs ne sont pas respectés (manquant)
 */
public class ErrorServer implements ServerResponse {

    @Serial
    private static final long serialVersionUID = 4L;

    private final String error;

    public ErrorServer(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
