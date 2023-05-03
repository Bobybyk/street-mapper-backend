package app.server;

import java.io.Serializable;
import java.text.Collator;
import java.util.Set;
import java.util.stream.Collectors;
import app.map.StationInfo;
import app.server.data.SuggestionStations;
import app.server.data.SuggestionStations.SuggestionKind;

/**
 * Calcule une suggestion de noms de stations à partir d'un certain préfixe
 */
public class SearchStation implements ServerActionCallback {
    /**
     * L'ensemble des noms de stations avec leurs informations.
     */
    private final Set<StationInfo> stationsInfo;
    /**
     * Le préfixe recherché
     */
    private final String prefix;
    /**
     * Le type de suggestion
     */
    private final SuggestionKind kind;

    /**
     * @param stationsInfo l'ensemble des noms de stations avec leurs informations.
     * @param prefix le préfixe recherché
     * @param kind le type de suggestion
     */
    public SearchStation(Set<StationInfo> stationsInfo, String prefix, SuggestionKind kind) {
        this.stationsInfo = stationsInfo;
        this.prefix = prefix;
        this.kind = kind;
    }

    @Override
    public Serializable execute() {
        Collator insenstiveStringComparator = Collator.getInstance();
        insenstiveStringComparator.setStrength(Collator.PRIMARY);
        Set<StationInfo> stations = stationsInfo.stream()
                .filter(lignedStation -> insenstiveStringComparator.compare(
                        lignedStation.getStationName().substring(0,
                                Math.min(lignedStation.getStationName().length(), prefix.length())),
                        prefix) == 0)
                .collect(Collectors.toSet());
        return new SuggestionStations(stations, kind);
    }
}
