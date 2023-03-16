package app.map;

import java.util.HashSet;
import java.util.List;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public final class Section {
    /**
     * La station de départ
     */
    private Station start;
    /**
     * La station d'arrivée
     */
    private Station arrival;
    /**
     * La distance entre les 2 stations
     */
    private final double distance;
    /**
     * La durée en seconde du trajet les 2 stations
     */
    private final int duration;
    /**
     * Le nom de la ligne
     */
    private final String line;

    /**
     * Liste des horaires de départ depuis la station start
     */
    private final HashSet<Time> departures = new HashSet<>();

    /**
     * Crée une section
     * 
     * @param start    la station de départ
     * @param arrival  la station d'arrivée
     * @param distance la longueur section
     * @param duration la durée en seconde de la section
     * @param line     le nom de la ligne
     */
    public Section(Station start, Station arrival, double distance, int duration, String line) {
        this.start = start;
        this.arrival = arrival;
        this.distance = distance;
        this.duration = duration;
        this.line = line;
    }

    public void addTimes(int hour, int minute) {
        departures.add(new Time(hour, minute, 0));
    }

    /**
     * Ajoute des horaires de départ
     * 
     * @param times une liste d'horaire
     */
    public void addTimes(List<Time> times) {
        departures.addAll(times);
    }

    public String getStartStationName() {
        return start.getName();
    }

    public Station getStart() {
        return start;
    }

    public Station getArrival() {
        return arrival;
    }

    public HashSet<Time> getDepartures() {
        return departures;
    }

    public int getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return String.format("%s --%s(%f, %d)--> %s", start.getName(), line, distance, duration, arrival.getName());
    }
}
