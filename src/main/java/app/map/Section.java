package app.map;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public final class Section {
    /**
     * La station de départ
     */
    private final Station start;
    /**
     * La station d'arrivée
     */
    private final Station arrival;
    /**
     * La distance entre les 2 stations
     */
    private final double distance;
    /**
     * La durée en seconde du trajet entre les 2 stations
     */
    private final int duration;

    /**
     * Crée une section
     *
     * @param start    la station de départ
     * @param arrival  la station d'arrivée
     * @param distance la longueur section
     * @param duration la durée en seconde de la section
     * @throws IllegalArgumentException si start ou arrival est `null`
     */
    public Section(Station start, Station arrival, double distance, int duration) throws IllegalArgumentException {
        if (start == null || arrival == null)
            throw new IllegalArgumentException();
        this.start = start;
        this.arrival = arrival;
        this.distance = distance;
        this.duration = duration;
    }

    public Station getStart() {
        return start;
    }

    public Station getArrival() {
        return arrival;
    }

    public int getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return String.format("%s --> %s (%f, %d)", start.getName(), distance, duration, arrival.getName());
    }
}
