package app.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public class Section implements Serializable {

    @Serial
    private static final long serialVersionUID = 7L;

    private final Station start;
    private final Station arrival;
    private final String line; // avec variant
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
        if (start == null || arrival == null || line == null)
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

    public Section changeLine(String line) {
        return new Section(start, arrival, line, distance, duration);
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
            return s.start.equals(start) && s.arrival.equals(arrival) && s.line.equals(line)
                    && s.distance == distance && s.duration == duration;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, arrival, line, distance, duration);
    }

    @Override
    public String toString() {
        return String.format("ligne %s à %s : %s --> %s (%d m, %s)", line,
                time != null ? time : "no:tm", start.getName(), arrival.getName(), distance,
                new Time(duration));
    }
}
