package app.server;

import java.io.Serializable;
import java.text.Collator;
import java.util.Set;
import java.util.stream.Collectors;
import app.map.StationInfo;
import app.server.data.SuggestionStations;
import app.server.data.SuggestionStations.SuggestionKind;

public class SearchStation implements ServerActionCallback {
    private final Set<StationInfo> stationsInfo;
    private final String prefix;
    private final SuggestionKind kind;

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
