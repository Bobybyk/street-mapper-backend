package server.map;

import java.io.Serial;
import java.io.Serializable;

/**
 * Classe représentant un temps
 */
public record Time(int hour, int minute, int second) implements Comparable<Time>, Serializable {

    @Serial
    private static final long serialVersionUID = 6L;

    /**
     * Nombre de secondes dans une journée
     */
    private static final int HOUR_IN_A_DAY = 3600 * 24;

    /**
     * Créer un nouveau temps
     *
     * @param hour le nombre des heures entre 0 et 23
     * @param minute le nombre des minutes entre 0 et 59
     * @param second le nombre des secondes entre 0 et 59
     * @throws IllegalArgumentException si les valeurs sont incorrectes
     */
    public Time {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59)
            throw new IllegalArgumentException();
    }

    /**
     * Convertie des secondes en un temps
     *
     * @param duration les secondes à convertir
     */
    public Time(int duration) {
        this((duration / 3600) % 24, (duration / 60) % 60, duration % 60);
    }

    /**
     * Créer un nouveau temps le nombre de seconde à 0
     *
     * @param hour le nombre des heures entre 0 et 23
     * @param minute le nombre des minutes entre 0 et 59
     */
    public Time(int hour, int minute) {
        this(hour, minute, 0);
    }

    /**
     * Créer une copie d'un temps
     *
     * @param t un temps
     */
    public Time(Time t) {
        this(t.hour, t.minute, t.second);
    }

    /**
     * Ajoute des secondes au temps et renvoie le nouveau temps.
     *
     * @param second le nombre de secondes à ajouter
     * @return un nouveau temps avec les secondes en plus
     */
    public Time addDuration(int second) {
        int s = this.second + second;
        int m = minute + (s / 60);
        int h = hour + (m / 60);
        return new Time(h % 24, m % 60, s % 60);
    }

    @Override
    public String toString() {
        return hour == 0 ? String.format("%02d:%02d", minute, second)
                : String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * @return le nombre de secondes depuis minuit
     */
    private int toSeconds() {
        return second + minute * 60 + hour * 3600;
    }

    @Override
    public int compareTo(Time time) {
        return toSeconds() - time.toSeconds();
    }

    /**
     * @param time le temps à atteindre
     * @return le nombre de secondes nécessaire pour atteindre {@code time}
     */
    public int durationTo(Time time) {
        int t1 = toSeconds();
        int t2 = time.toSeconds();
        int diff = t2 - t1;
        while (diff < 0) {
            diff += HOUR_IN_A_DAY;
        }
        return diff;
    }
}
