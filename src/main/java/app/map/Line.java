package app.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Classe représentant une ligne
 */
public final class Line {

    static class StartStationNotFoundException extends Exception {
        public StartStationNotFoundException(String station, String line, String variant) {
            super(String.format("La station %s n'est pas sur la ligne %s variant %s", station, line,
                    variant));
        }
    }

    static class DifferentStartException extends Exception {
        public DifferentStartException(String line, String variant, String s1, String s2) {
            super(String.format(
                    "Il y plusieurs stations de départ pour la ligne %s variant %s : %s et %s",
                    line, variant, s1, s2));
        }
    }

    /**
     * Le nom de la ligne
     */
    private final String name;
    /**
     * Le variant de la ligne
     */
    private final String variant;
    /**
     * La section de départ
     */
    private Section start;
    /**
     * La liste des horaires de départ de la section de départ
     */
    private final ArrayList<Time> departures;
    /**
     * Chaque section est associée à la durée nécessaire pour arriver à la fin de la section depuis
     * le début de la section de départ
     */
    private final HashMap<Section, Integer> sections;
    /**
     * le temps d'attente entre chaque section à chaque arrêt (en secondes)
     */
    private static final int WAITING_TIME = 20;

    /**
     * Créer une nouvelle ligne vide.
     *
     * @param name le nom de la ligne
     * @param variant le nom du variant
     */
    public Line(String name, String variant) {
        if (name == null)
            throw new IllegalArgumentException();
        this.name = name;
        this.variant = variant;
        this.start = null;
        sections = new HashMap<>();
        departures = new ArrayList<>();
    }

    /**
     * Détermine la section de départ à partir du nom de la station de départ et modifie l'attribut
     * associé `start`.
     *
     * @param stationName le nom de la station de départ de la ligne
     * @throws StartStationNotFoundException s'il n'y a pas de section commençant à une station à ce
     *         nom
     * @throws DifferentStartException s'il y a déjà une section de départ qui ne commence pas par à
     *         la même station
     * @throws IllegalArgumentException si stationName est `null`
     */
    public void setStart(String stationName) throws IllegalArgumentException,
            StartStationNotFoundException, DifferentStartException {
        if (stationName == null)
            throw new IllegalArgumentException();
        if (start == null) {
            Optional<Section> station = sections.keySet().stream()
                    .filter(s -> s.getStart().getName().equals(stationName)).findAny();
            if (station.isEmpty())
                throw new StartStationNotFoundException(stationName, name, variant);
            start = station.get();
        } else {
            String actual = start.getStart().getName();
            if (!actual.equals(stationName))
                throw new DifferentStartException(name, variant, actual, stationName);
        }
    }

    public String getName() {
        return name;
    }

    public String getVariant() {
        return variant;
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
    public List<Section> getSections() {
        return new ArrayList<>(sections.keySet());
    }

    /**
     * @return l'attribut de type HashMap sections
     */
    public HashMap<Section, Integer> getSectionsMap() {
        return sections;
    }

    /**
     * Ajoute une section à la ligne. La durée entre la section et la section de départ est
     * initialisée à -1.
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
     * @param hour les heures de l'horaire
     * @param minute les minutes de l'horaire
     * @throws IllegalArgumentException si hour n'est pas entre 0 et 23 et minute entre 0 et 59
     *         (inclus)
     */
    public void addDepartureTime(int hour, int minute) throws IllegalArgumentException {
        Time time = new Time(hour, minute, 0);
        if (!departures.contains(time))
            this.departures.add(time);
    }

    public List<Time> getDepartures() {
        return new ArrayList<>(departures);
    }

    /**
     * @param section la section dont l'horaire est à déterminée
     * @param time l'horaire minimal
     * @return l'horaire du prochain départ à section après time, `null` si time est `null` ou
     *         departures est vide
     */
    public Time getNextTime(Section section, Time time) {
        int durationFromDeparture = sections.get(section);
        if (time == null || departures.isEmpty() || durationFromDeparture == -1)
            return null;
        for (Time t : departures) {
            Time departTime = t.addDuration(durationFromDeparture);
            if (time.compareTo(departTime) < 0)
                return departTime;
        }
        return departures.get(0).addDuration(durationFromDeparture);
    }

    public void updateSectionsTime() {
        if (start == null)
            return;
        Section curentSection = start;
        int curentTime = curentSection.getDuration();
        int nbSections = sections.size();
        int nbSectionDone = 1;
        while (nbSectionDone < nbSections) {
            for (Section s : getSections()) {
                if (s.getStart().getName().equals(curentSection.getArrival().getName())) {
                    sections.put(s, sections.get(curentSection) + s.getDuration() + WAITING_TIME);
                    curentSection = s;
                }
                nbSectionDone++;
            }
        }
    }

    /**
     * renvoit la list des horaires de la station d'arrivé de la section
     *
     * @param section la section qui a la station visé en arrivé
     */
    public LinkedList<Time> getDepartureTimeFromStation(Section section) {
        LinkedList<Time> timeTable = new LinkedList<>();
        int timeToStation = sections.get(section).intValue() + WAITING_TIME;
        for (Time t : departures) {
            timeTable.addLast(t.addDuration(timeToStation));
        }
        return timeTable;
    }
}
