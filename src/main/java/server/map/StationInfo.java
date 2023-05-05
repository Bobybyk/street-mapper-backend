package server.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Une station associée à sa ligne
 */
public class StationInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    /**
     * Le nom de la station
     */
    private final String stationName;

    /**
     * Nom des lignes qui ayant un arrêt à {@code stationName}
     */
    private final Set<String> lines;

    public StationInfo(String stationName, Collection<String> collection) {
        this.stationName = stationName;
        this.lines = new HashSet<>(collection);
    }

    public StationInfo(String stationName) {
        this.stationName = stationName;
        this.lines = new HashSet<>();
    }

    public Set<String> getLines() {
        return new HashSet<>(lines);
    }

    public String getStationName() {
        return stationName;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StationInfo si) {
            // Pour plus tard: Si la map s'agrandit, peut-etre aussi verifier en fonction de la
            // distance entre les 2 stations
            return stationName.equals(si.stationName) && lines.containsAll(si.lines)
                    && lines.size() == si.lines.size();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName, lines);
    }

    @Override
    public String toString() {
        return "ligne : " + stationName + ", station : { " + String.join(", ", lines) + " }";
    }
}
