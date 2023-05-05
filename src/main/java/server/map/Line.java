package server.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Classe représentant une ligne
 */
public final class Line {

    /**
     * La station n'est pas sur la ligne
     */
    static class StationNotFoundException extends Exception {
        public StationNotFoundException(String station, String line, String variant) {
            super(String.format("La station %s n'est pas sur la ligne %s variant %s", station, line,
                    variant));
        }
    }

    /**
     * Il y a différentes stations de départ sur la ligne
     */
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
     * La dernière section
     */
    private Section last;
    /**
     * La liste des horaires de départ de la section de départ
     */
    private final SortedSet<Time> departures;
    /**
     * Chaque section est associée à la durée nécessaire pour arriver à la fin de la section depuis
     * le début de la section de départ
     */
    private final Map<Section, Integer> sections;
    /**
     * Le temps d'attente entre chaque section à chaque arrêt (en secondes)
     */
    private static final int WAITING_TIME = 20;

    /**
     * Créer une nouvelle ligne vide.
     *
     * @param name le nom de la ligne
     * @param variant le nom du variant
     */
    public Line(String name, String variant) {
        this(name, variant, new HashMap<>());
    }

    /**.
     * Crée une nouvelle ligne
     * 
     * @param name le nom de la ligne
     * @param variant le nom du variant
     * @param sections une map associant une section à sa duree pour arriver à sa fin de la section de départ
     */
    public Line(String name, String variant, Map<Section, Integer> sections) {
        if (name == null)
            throw new IllegalArgumentException();
        this.name = name;
        this.variant = variant;
        this.start = null;
        this.last = null;
        this.sections = new HashMap<>(sections);
        this.departures = new TreeSet<>();
    }

    /**
     * Crée une nouvelle ligne en remettant aux valeures initiales {@code start}, {@code last} et {@code departures}
     * @return la nouvelle line créée
     */
    public Line resetDeparturesTimeData(){
        HashMap<Section, Integer> sections = this.sections.entrySet()
        .stream()
        .reduce(new HashMap<>(), (acc, entry) -> {
            acc.put(entry.getKey(), null);
            return acc;
        }, (lhs, rhs) -> {
            lhs.putAll(rhs);
            return lhs;
        });
        return new Line(this.name, this.variant, sections);
    }

    /**
     * Détermine la section de départ à partir du nom de la station de départ et modifie l'attribut
     * associé {@code start}
     *
     * @param stationName le nom de la station de départ de la ligne
     * @throws StartStationNotFoundException s'il n'y a pas de section commençant à une station à ce
     *         nom
     * @throws DifferentStartException s'il y a déjà une section de départ qui ne commence pas par à
     *         la même station
     * @throws IllegalArgumentException si {@code stationName} est {@code null}
     */
    public void setStart(String stationName)
            throws IllegalArgumentException, StationNotFoundException, DifferentStartException {
        if (stationName == null)
            throw new IllegalArgumentException();
        if (start == null) {
            Optional<Section> station = sections.keySet().stream()
                    .filter(s -> s.getStart().getName().equals(stationName)).findAny();
            if (station.isEmpty())
                throw new StationNotFoundException(stationName, name, variant);
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

    public Section getStart() {
        return start;
    }

    public Section getLast() {
        return last;
    }

    /**
     * @return la liste des sections de la ligne
     */
    public List<Section> getSections() {
        return new ArrayList<>(sections.keySet());
    }

    /**
     * @return la map des sections associées à leur durée depuis la station de départ
     */
    public Map<Section, Integer> getSectionsMap() {
        return new HashMap<>(sections);
    }

    /**
     * Ajoute une section à la ligne. La durée entre la section et la section de départ est
     * initialisée à {@code null}.
     *
     * @param section une section à ajouter à la ligne
     * @throws IllegalArgumentException si section est {@code null}
     */
    public void addSection(Section section) throws IllegalArgumentException {
        if (section == null)
            throw new IllegalArgumentException();
        sections.put(section, null);
    }

    /**
     * Ajoute un horaire de départ de la section de départ de la ligne.
     *
     * @param hour les heures de l'horaire
     * @param minute les minutes de l'horaire
     * @throws IllegalArgumentException si {@code hour} n'est pas entre 0 et 23 et {@code minute}
     *         entre 0 et 59 (inclus)
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
     * @return l'horaire du prochain départ à {@code section} après {@code time}, {@code null} si
     *         {@code time} est {@code null} ou {@code departures} est vide
     */
    public Time getNextTime(Section section, Time time) {
        Integer duration = sections.get(section);
        if (duration == null || time == null || departures.isEmpty())
            return null;
        duration -= section.getDuration();
        for (Time t : departures) {
            Time departTime = t.addDuration(duration);
            if (time.compareTo(departTime) <= 0)
                return departTime;
        }
        return departures.first().addDuration(duration);
    }

    /**
     * @param section une section
     * @return la section suivante ou {@code null} s'il n'y en a pas
     */
    private Section getNextSection(Section section) {
        Optional<Section> next = sections.keySet().stream()
                .filter(s -> s.getStart().equals(section.getArrival())).findAny();
        return next.orElse(null);
    }

    /**
     * Calcule le temps nécessaire entre la station de départ et toutes les autres stations de la
     * ligne, les résultats sont mis dans {@code sections}. Si la station de départ n'est pas
     * définie, ne fait rien.
     */
    public void updateSectionsTime() {
        if (start == null)
            return;
        int duration = start.getDuration();
        sections.put(start, duration);
        Section section = getNextSection(start);
        while (section != null && sections.get(section) == null) {
            duration += WAITING_TIME + section.getDuration();
            sections.put(section, duration);
            last = section;
            section = getNextSection(section);
        }
    }

    /**
     * @param section une section
     * @param envoie la liste des horaires de départ de {@code section}
     */
    public List<Time> getDepartureTime(Section section) {
        List<Time> times = new ArrayList<>();
        Integer durationToArrival = sections.get(section);
        if (durationToArrival != null) {
            int duration = durationToArrival - section.getDuration();
            for (Time t : departures) {
                times.add(t.addDuration(duration));
            }
        }
        return times;
    }
}
