package app.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Classe représentant une ligne
 */
public final class Line {

    public static class StartStationNotFoundException extends Exception {
        public StartStationNotFoundException(String station, String line, int variant) {
            super(String.format("La station %s n'est pas sur la ligne %s variant %d", station, line, variant));
        }
    }

    public static class DifferentStartException extends Exception {
        public DifferentStartException(String line, int variant, String s1, String s2) {
            super(String.format("Il y plusieurs stations de départ pour la ligne %s variant %d : %s et %s", line,
                    variant, s1, s2));
        }
    }

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
     * @throws StartStationNotFoundException s'il n'y a pas de section commencant à
     *                                       une station à ce nom
     * @throws DifferentStartException       s'il y a déjà une section de départ qui
     *                                       ne commence pas par à la même station
     * @throws IllegalArgumentException      si stationName est `null`
     */
    public void setStart(String stationName)
            throws IllegalArgumentException, StartStationNotFoundException, DifferentStartException {
        if (stationName == null)
            throw new IllegalArgumentException();
        if (start == null) {
            Optional<Section> start = sections.keySet().stream().filter(s -> s.getStart().getName().equals(stationName))
                    .findAny();
            if (start.isEmpty())
                throw new StartStationNotFoundException(stationName, name, variant);
            this.start = start.get();
        } else {
            String actual = start.getStart().getName();
            if (!actual.equals(stationName))
                throw new DifferentStartException(name, variant, actual, stationName);
        }
    }

    /**
     * Renvoie la section de départ de la ligne.
     *
     * @return la section de départ ou `null` si elle n'a pas été définie
     */
    public Section getStart() {
        return start;
    }

    /**
     * @return la liste des sections de la ligne
     */
    public ArrayList<Section> getSections() {
        return new ArrayList<>(sections.keySet());
    }

    /**
     * Ajoute une section à la ligne.
     * La durée entre la section et la section de départ est initialisée à -1.
     *
     * @param section une section appartenant à la ligne
     * @throws IllegalArgumentException si section est `null`
     */
    public void addSection(Section section) throws IllegalArgumentException {
        if (section == null)
            throw new IllegalArgumentException();
        sections.put(section, -1);
    }

    /**
     * Ajoute un horaire de départ de la section de départ de la ligne.
     *
     * @param hour   les heures de l'horaire
     * @param minute les minutes de l'horaire
     * @throws IllegalArgumentException si hour n'est pas entre 0 et 23 et minute
     *                                  entre 0 et 59 (inclus)
     */
    public void addDepartureTime(int hour, int minute) throws IllegalArgumentException {
        Time time = new Time(hour, minute, 0);
        if (!departures.contains(time))
            this.departures.add(time);
    }

    public ArrayList<Time> getDepartures() {
        return new ArrayList<>(departures);
    }
}
