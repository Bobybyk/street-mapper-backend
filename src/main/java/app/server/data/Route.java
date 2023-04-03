package app.server.data;

import app.map.Section;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;

public class Route implements Serializable {

    /**
     * Class route qui represente le trajet prix par l'utilisateur
     */
    @Serial
    private static final long serialVersionUID = 2L;

    private final LinkedList<Section> pathDistOpt;

    public Route(LinkedList<Section> pathDistOpt) {
        this.pathDistOpt = pathDistOpt;
    }

    public LinkedList<Section> getPathDistOpt() {
        return pathDistOpt;
    }

}

