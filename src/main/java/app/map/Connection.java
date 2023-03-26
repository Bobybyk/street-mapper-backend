package app.map;

import java.util.ArrayList;

/**
 * Classe représentant les correspondances d'une stations
 */
public final class Connection {
    /**
     * Une station
     */
    private final Station station;
    /**
     * Les correspondances de la station
     */
    private final ArrayList<Section> sections;

    /**
     * Créer une correspondance
     *
     * @param station la station de la correspondance
     * @throws IllegalArgumentException si station est `null`
     */
    public Connection(Station station) throws IllegalArgumentException {
        if (station == null)
            throw new IllegalArgumentException();
        this.station = station;
        sections = new ArrayList<>();
    }

    public Station getStation() {
        return station;
    }

    public ArrayList<Section> getSections() {
        return new ArrayList<>(sections);
    }

    /**
     * Ajoute une section à la correspondance si elle n'y est pas déjà
     *
     * @param section la section à ajouter
     * @throws IllegalArgumentException si la section est `null` ou ne commence pas
     *                                  à cette station
     */
    public void addSection(Section section) throws IllegalArgumentException {
        if (section == null || section.getStart() != station)
            throw new IllegalArgumentException();
        if (!sections.contains(section))
            sections.add(section);
    }
}
