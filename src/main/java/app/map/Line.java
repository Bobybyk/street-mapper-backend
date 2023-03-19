package app.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Classe représentant une ligne
 */
public final class Line {
    /**
     * Le nom de la ligne
     */
    private final String name;
    /**
     * La variant de la ligne
     */
    private final int variant;
    /**
     * La section de départ
     */
    private Section start;
    /**
     * La liste des horaires de départ de la section de départ
     */
    private final ArrayList<Time> departures;
    /**
     * Chaque section est associée à la durée nécessaire pour arriver à la fin de la
     * section depuis le début de la section de départ
     */
    private final HashMap<Section, Integer> sections;

    /**
     * Créer une nouvelle ligne vide.
     * 
     * @param name    le nom de la ligne
     * @param variant le numéro du variant
     * @throws IllegalArgumentException si le nom de la ligne est `null`
     */
    public Line(String name, int variant) {
        if (name == null)
            throw new IllegalArgumentException();
        this.name = name;
        this.variant = variant;
        this.start = null;
        sections = new HashMap<>();
        departures = new ArrayList<>();
    }

    /**
     * Détermine la section de départ à partir du nom de la station de départ et
     * modifie l'attribut associé `start`.
     * 
     * @param stationName le nom de la station de départ de la ligne
     * @throws IllegalArgumentException s'il n'y a pas de section commencant à une
     *                                  station à ce nom
     */
    public void setStart(String stationName) {
        Optional<Section> start = sections.keySet().stream().filter(s -> s.getStart().getName().equals(stationName))
                .findAny();
        if (start.isEmpty())
            throw new IllegalArgumentException(
                    String.format("La station %s n'est pas sur la ligne %s variant %d", stationName, name, variant));
        this.start = start.get();
    }

    /**
     * Renvoie la section de départ de la ligne.
     * 
     * @return la section de départ ou `null` si elle n'a pas été définie
     */
    public Section getStart() {
        return start;
    }

    public boolean isStartingAt(String stationName) {
        return start != null && start.getStart().getName().equals(stationName);
    }

    /**
     * Ajoute une section à la ligne.
     * La durée entre la section et la section de départ est initialisée à -1.
     * 
     * @param section une section appartenant à la ligne
     */
    public void addSection(Section section) {
        sections.put(section, -1);
    }

    /**
     * Ajoute un horaire de départ de la section de départ de la ligne.
     * 
     * @param hour   les heures de l'horaire
     * @param minute les minutes de l'horaire
     */
    public void addDepartureTime(int hour, int minute) {
        this.departures.add(new Time(hour, minute, 0));
    }
}