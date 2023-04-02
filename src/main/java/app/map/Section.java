package app.map;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public record Section(
        /**
         * La station de départ
         */
        Station start,
        /**
         * La station d'arrivée
         */
        Station arrival,
        /**
         * La distance entre les 2 stations
         */
        double distance,
        /**
         * La durée en seconde du trajet entre les 2 stations
         */
        int duration) {

    /**
     * Crée une section
     *
     * @param start    la station de départ
     * @param arrival  la station d'arrivée
     * @param distance la longueur section
     * @param duration la durée en seconde de la section
     * @throws IllegalArgumentException si start ou arrival est `null`
     */
    public Section(Station start, Station arrival, double distance, int duration) {
        if (start == null || arrival == null)
            throw new IllegalArgumentException();
        this.start = start;
        this.arrival = arrival;
        this.distance = distance;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("%s --> %s (%f, %d)", start.name(), distance, duration, arrival.name());
    }
}
