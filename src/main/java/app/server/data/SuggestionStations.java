package app.server.data;

import java.io.Serial;
import java.io.Serializable;
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
        stations = collection.stream()
        .filter(lignedStation -> 
            lignedStation.getStationName().startsWith(prefixStation)
        ).collect(Collectors.toSet());
    }

    public Set<LignedStation> getStations() {
        return stations;
    }

}
