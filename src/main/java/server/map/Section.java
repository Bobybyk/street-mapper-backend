package server.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public class Section implements Serializable {

    @Serial
    private static final long serialVersionUID = 10L;

    /**
     * La station de départ
     */
    private final Station start;
    /**
     * La station d'arrivée
     */
    private final Station arrival;
    /**
     * Le nom de ligne
     */
    private String line;
    /**
     * L'horaire de départ
     */
    private Time time;
    /**
     * La longueur de la section en mètres
     */
    private final int distance;
    /**
     * La durée de la section en seconde
     */
    private final int duration;

    /**
     * Crée une section
     *
     * @param start la station de départ
     * @param arrival la station d'arrivée
     * @param line le nom de la ligne de la section
     * @param time l'horaire de départ la section
     * @param distance la longueur de la section
     * @param duration la durée en seconde de la section
     * @throws IllegalArgumentException si {@code start} ou {@code arrival} ou {@code line} est
     *         {@code null}
     */
    public Section(Station start, Station arrival, String line, Time time, int distance, int duration) {
        if (start == null || arrival == null)
            throw new IllegalArgumentException();
        this.start = start;
        this.arrival = arrival;
        this.line = line;
        this.time = null;
        this.distance = distance;
        this.duration = duration;
    }

    /**
     * Crée une section en mettant {@code time} à {@code null}
     *
     * @param start la station de départ
     * @param arrival la station d'arrivée
     * @param line le nom de la ligne de la section
     * @param distance la longueur de la section
     * @param duration la durée en seconde de la section
     * @throws IllegalArgumentException si {@code start} ou {@code arrival} ou {@code line} est
     *         {@code null}
     */
    public Section(Station start, Station arrival, String line, int distance, int duration) {
        this(start, arrival, line, null, distance, duration);
    }

    /**
     * Crée une copie de la section
     *
     * @param s une section à copier
     */
    public Section(Section s) {
        this(s.start, s.arrival, s.line, s.time, s.distance, s.duration);
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Station getStart() {
        return start;
    }

    public Station getArrival() {
        return arrival;
    }

    public String getLine() {
        return line;
    }

    public Time getTime() {
        return time;
    }

    /**
     * @return l'heure d'arrivé ou {@code null} si {@code time} est {@code null}
     */
    public Time getArrivalTime() {
        return time == null ? null : time.addDuration(duration);
    }

    public void setTime(Time time) {
        this.time = time;
    }

    /**
     * @param nextSection une section
     * @return la distance entre l'arrivée de cette section et l'arrivée de {@code nextSection}
     * @throws IllegalArgumentException si {@code nextSection} est {@code null}
     */
    public int distanceTo(Section nextSection) throws IllegalArgumentException {
        if (nextSection == null)
            throw new IllegalArgumentException();
        return arrival.distanceBetween(nextSection.start) + nextSection.distance;
    }

    /**
     * @param nextSection une section
     * @return le temps entre l'arrivée de cette section et l'arrivée de {@code nextSection}
     * @throws IllegalArgumentException si {@code nextSection} ou l'un des heures d'arrivées des
     *         sections est {@code null}
     */
    public int durationTo(Section nextSection) throws IllegalArgumentException {
        if (nextSection == null)
            throw new IllegalArgumentException();
        return arrival.durationBetween(nextSection.start)
                + getArrivalTime().durationTo(nextSection.getArrivalTime());
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Section s)
            return s.start.equals(start) && s.arrival.equals(arrival)
                    && (line == s.line || (s.line != null && s.line.equals(line)))
                    && s.distance == distance && s.duration == duration;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, arrival, line, distance, duration);
    }

    @Override
    public String toString() {
        return String.format("%s à %s : %s --> %s (%d m, %s)",
                line == null ? "à pied" : "ligne " + line, time != null ? time : "no:tm",
                start.getName(), arrival.getName(), distance, new Time(duration));

    }

    public static List<Section> sectionsToTrajet(List<Section> sections) {
        if (sections == null || sections.isEmpty())
            return sections;

        List<Section> trajet = new ArrayList<>();
        Section first = sections.get(0);
        Station start = first.start;
        Station arrival = first.arrival;
        String line = first.line;
        Time time = first.time;
        int distance = 0;
        int duration = 0;

        for (Section s : sections) {
            if (line != null && line.equals(s.line)) {
                arrival = s.arrival;
                distance += s.distance;
                duration += s.duration;
            } else {
                Section toAdd = new Section(start, arrival, line, null, distance, duration);
                toAdd.setTime(time);
                trajet.add(toAdd);
                start = s.start;
                arrival = s.arrival;
                line = s.line;
                time = s.time;
                distance = s.distance;
                duration = s.duration;
            }
        }
        Section toAdd = new Section(start, arrival, line, null, distance, duration);
        toAdd.setTime(time);
        trajet.add(toAdd);
        return trajet;
    }
}
