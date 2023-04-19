package app.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public class Section implements Serializable {

    @Serial
    private static final long serialVersionUID = 6L;

    private final Station start;
    private final Station arrival;
    private final String line;
    private Time time;
    private final int distance; // en mètre
    private final int duration; // en seconde

    /**
     * Crée une section
     *
     * @param start    la station de départ
     * @param arrival  la station d'arrivée
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

    public void setTime(Time time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    /**
     * Calcul la distance en mètre entre l'arrivé de cette section à l'arrivée de
     * nextSection
     *
     * @param nextSection une section
     * @return la distance entre l'arrivé de cette section à l'arrivée de
     *         nextSection
     * @throws IllegalArgumentException si nextSection est null
     */
    public int distanceTo(Section nextSection) throws IllegalArgumentException {
        if (nextSection == null)
            throw new IllegalArgumentException();
        return arrival.getCoordinate().getDistance(nextSection.start.getCoordinate()) + nextSection.distance;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Section s)
            return s.start.equals(start) && s.arrival.equals(arrival) && s.line.equals(line) && s.distance == distance
                    && s.duration == duration;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, arrival, line, distance, duration);
    }

    @Override
    public String toString() {
        return String.format("ligne %s à %s : %s --> %s (%d m, %s)",
                line, time != null ? time : "no:tm", start.getName(), arrival.getName(), distance,
                new Time(duration));
    }
}
