package app.server.data;

import java.io.Serial;
import java.io.Serializable;
import java.text.Collator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import app.map.LignedStation;


/**
 * Classe représentant l'ensemble des stations (station + ligne) coorespondant la demande du client
 * 
 */
public class SuggestionStations implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Set<LignedStation> stations;

    public SuggestionStations(String prefixStation, Collection <? extends LignedStation> collection) {
        Collator insenstiveStringComparator = Collator.getInstance();
        insenstiveStringComparator.setStrength(Collator.PRIMARY);

        stations = collection.stream()
        .filter(lignedStation ->
            insenstiveStringComparator.compare(
                lignedStation.getStationName().substring(
                    0,
                    Math.min(
                        lignedStation.getStationName().length(),
                        prefixStation.length()
                    )
                ),
                prefixStation
            ) == 0
        ).collect(Collectors.toSet());
    }

    public Set<LignedStation> getStations() {
        return stations;
    }

}
