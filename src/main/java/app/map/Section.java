package app.map;

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
    private static final long serialVersionUID = 8L;

    private final Station start;
    private final Station arrival;
    private String line;
    private Time time;
    private final int distance; // en mètre
    private final int duration; // en seconde

    /**
     * Crée une section
     *
     * @param start la station de départ
     * @param arrival la station d'arrivée
     * @param line le nom de la ligne de la section
     * @param distance la longueur section
     * @param duration la durée en seconde de la section
     * @throws IllegalArgumentException si start ou arrival est `null`
     */
    public Section(Station start, Station arrival, String line, int distance, int duration) {
        if (start == null || arrival == null)
            throw new IllegalArgumentException();
        this.start = start;
        this.arrival = arrival;
        this.line = line;
        this.time = null;
        this.distance = distance;
        this.duration = duration;
    }

    public Section(Section s) {
        this(s.start, s.arrival, s.line, s.distance, s.duration);
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

    public Time getArrivalTime() {
        return time == null ? null : time.addDuration(duration);
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    /**
     * Calcul la distance en mètre entre l'arrivé de cette section à l'arrivée de nextSection
     *
     * @param nextSection une section
     * @return la distance entre l'arrivé de cette section à l'arrivée de nextSection
     * @throws IllegalArgumentException si nextSection est null
     */
    public int distanceTo(Section nextSection) throws IllegalArgumentException {
        if (nextSection == null)
            throw new IllegalArgumentException();
        return arrival.distanceBetween(nextSection.start) + nextSection.distance;
    }

    /**
     * Calcul la temps en seconde entre l'arrivé de cette section à l'arrivée de nextSection
     *
     * @param nextSection une section
     * @return la temps entre l'arrivé de cette section à l'arrivée de nextSection
     * @throws IllegalArgumentException si nextSection est null
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
        Station start = first.getStart();
        Station arrival = first.getArrival();
        String line = first.getLine();
        Time time = first.getTime();
        int distance = 0;
        int duration = 0;

        for (Section s : sections) {
            if (line != null && line.equals(s.getLine())) {
                arrival = s.getArrival();
                distance += s.getDistance();
                duration += s.getDuration();
            } else {
                Section toAdd = new Section(start, arrival, line, distance, duration);
                toAdd.setTime(time);
                trajet.add(toAdd);
                start = s.getStart();
                arrival = s.getArrival();
                line = s.getLine();
                time = s.getTime();
                distance = s.getDistance();
                duration = s.getDuration();
            }
        }
        Section toAdd = new Section(start, arrival, line, distance, duration);
        toAdd.setTime(time);
        trajet.add(toAdd);
        return trajet;
    }
}
