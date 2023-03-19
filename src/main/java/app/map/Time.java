package app.map;

/**
 * Classe représentant un temps
 */
public final class Time {
    /**
     * Le nombre des heures
     */
    private final int hour;
    /**
     * Le nombre des minutes
     */
    private final int minute;
    /**
     * Le nombre des secondes
     */
    private final int second;

    /**
     * Créer un nouveau temps
     * 
     * @param hour   le nombre des heures entre 0 et 23
     * @param minute le nombre des minutes entre 0 et 59
     * @param second le nombre des secondes entre 0 et 59
     * @throws IllegalArgumentException si les valeurs sont incorrectes
     */
    public Time(int hour, int minute, int second) throws IllegalArgumentException {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59)
            throw new IllegalArgumentException();
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Ajoute des secondes au temps et renvoie le nouveau temps.
     * 
     * @param second le nombre de secondes à ajouter
     * @return un nouveau Time avec les secondes en plus
     */
    public Time addDuration(int second) {
        int s = this.second + second;
        int m = minute + (s / 60);
        int h = hour + (m / 60);
        return new Time(h % 24, m % 60, s % 60);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Time t) {
            return this.hour == t.hour && this.minute == t.minute && this.second == t.second;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}