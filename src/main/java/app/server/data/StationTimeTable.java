package app.server.data;

import app.map.Time;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;


/**
 * Class StationTimeTable qui represente les horraires d'une station à partir d'une certaine heure
 */
public class StationTimeTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private final LinkedList<Time> timeTable;

    public StationTimeTable(LinkedList<Time> timeTable) {
        this.timeTable = timeTable;
    }

    public LinkedList<Time> getPathDistOpt() {
        return timeTable;
    }

}

