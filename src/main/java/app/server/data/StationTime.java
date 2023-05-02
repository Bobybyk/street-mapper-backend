package app.server.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import app.map.Time;

public class StationTime implements Serializable {
    private final String line;
    private final String station;
    private final Time time;

    @Serial
    private static final long serialVersionUID = 1L;

    public StationTime(String line, String station, Time time) {
        this.line = line;
        this.station = station;
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("ligne %s vers %s à %s", line, station, time);
    }

    public String getStation() {
        return station;
    }

    public String getLine() {
        return line;
    }

    public Time getTime() {
        return time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StationTime s)
            return (line == s.line || (s.line != null && s.line.equals(line)))
                    && s.station.equals(station) && s.time.equals(time);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, station, time);
    }
}
