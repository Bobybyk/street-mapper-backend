package app.server.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * Classe représentant les horaires d'une station à partir d'une certaine heure
 */
public class DepartureTimes implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private final List<StationTime> times;

    public DepartureTimes(List<StationTime> times) {
        this.times = times;
    }

    public List<StationTime> getTimes() {
        return times;
    }
}
