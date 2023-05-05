package server.data;

import java.io.Serial;
import java.util.List;


/**
 * Classe représentant les horaires d'une station à partir d'une certaine heure
 */
public class DepartureTimes implements ServerResponse {

    @Serial
    private static final long serialVersionUID = 4L;

    private final List<StationTime> times;

    public DepartureTimes(List<StationTime> times) {
        this.times = times;
    }

    public List<StationTime> getTimes() {
        return times;
    }
}
