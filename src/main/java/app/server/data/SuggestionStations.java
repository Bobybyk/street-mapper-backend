package app.server.data;

import java.io.Serial;
import java.io.Serializable;
import java.text.Collator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import app.map.StationInfo;


/**
 * Classe représentant l'ensemble des stations (station + ligne) coorespondant la demande du client
 *
 */
public class SuggestionStations implements Serializable {

    public static enum SuggestionKind {
        DEPART,
        ARRIVAL;

        /**
         * Retourne l'énumération correspondant à {@code s}, {@code null} sinon
         * @param s Nom de l'énumération
         * @return cas correspondant au nom de l'énumération
         */
        public static SuggestionKind ofString(String s){
            return switch(s) {
                case "DEPART" -> DEPART;
                case "ARRIVAL" -> ARRIVAL;
                default -> null;
            };
        }
    }

    @Serial
    private static final long serialVersionUID = 3L;

    private Set<StationInfo> stations;
    private SuggestionKind kind;

    public SuggestionStations(String prefixStation, SuggestionKind kind, Collection <? extends StationInfo> collection) {
        this.kind = kind;
        Collator insenstiveStringComparator = Collator.getInstance();
        insenstiveStringComparator.setStrength(Collator.PRIMARY);

        stations =
                collection
                        .stream().filter(
                                lignedStation -> insenstiveStringComparator
                                        .compare(
                                                lignedStation.getStationName().substring(0,
                                                        Math.min(
                                                                lignedStation.getStationName()
                                                                        .length(),
                                                                prefixStation.length())),
                                                prefixStation) == 0)
                        .collect(Collectors.toSet());
    }

    public Set<StationInfo> getStations() {
        return stations;
    }

    public SuggestionKind getKind() {
        return kind;
    }

}
