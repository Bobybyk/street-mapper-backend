package server.data;

import java.io.Serial;
import java.util.List;
import server.map.Section;


/**
 * Classe représentant le trajet pris par l'utilisateur
 */
public class Route implements ServerResponse {

    @Serial
    private static final long serialVersionUID = 5L;

    private final List<Section> pathDistOpt;

    public Route(List<Section> pathDistOpt) {
        this.pathDistOpt = pathDistOpt;
    }

    public List<Section> getPathDistOpt() {
        return pathDistOpt;
    }
}
