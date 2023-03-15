package app.map;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe représentant la carte
 */
public final class Map {
    /**
     * Map où chaque station est associée aux sections dont le départ est cette
     * station
     */
    private final HashMap<Station, ArrayList<Section>> map = new HashMap<>();

}
