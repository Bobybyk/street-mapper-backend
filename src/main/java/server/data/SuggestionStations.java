package server.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import server.map.StationInfo;

/**
 * Classe représentant l'ensemble des stations (station + ligne) correspondant la demande du client
 */
public class SuggestionStations implements Serializable {

    public enum SuggestionKind {
        DEPART, ARRIVAL;

        /**
         * Retourne l'énumération correspondant à {@code s}, {@code null} sinon
         *
         * @param s Nom de l'énumération
         * @return cas correspondant au nom de l'énumération
         */
        public static SuggestionKind ofString(String s) {
            return switch (s) {
                case "DEPART" -> DEPART;
                case "ARRIVAL" -> ARRIVAL;
                default -> null;
            };
        }
    }

    @Serial
    private static final long serialVersionUID = 4L;

    private final Set<StationInfo> stations;
    private final SuggestionKind kind;

    public SuggestionStations(Set<StationInfo> stations, SuggestionKind kind) {
        this.stations = stations;
        this.kind = kind;
    }

    public Set<StationInfo> getStations() {
        return stations;
    }

    public SuggestionKind getKind() {
        return kind;
    }
}
