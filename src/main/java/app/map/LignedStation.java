package app.map;

import java.io.Serial;
import java.io.Serializable;

/**
 * Une station associeée à sa ligne
 */

public class LignedStation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Station station;

    private final String line;

    public LignedStation(Station station, String line) {
        this.station = station;
        this.line = line;
    }

    public String getLine() {
        return line;
    }
    
    public Station getStation() {
        return station;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LignedStation ls) {
            // Pour plus tard: Si la map s'agrandit, peut-etre aussi verifier en fonction de la distance entre les 2 stations
            return this.station.name().equals(ls.station.name()) && this.line.equals(ls.line);
        } 
        return false;
    }

    @Override
    public String toString() {
        return String.format("ligne : %s, station : %s", line, station.toString());
    }
}