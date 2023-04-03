package app.map;

import java.io.Serial;
import java.io.Serializable;

/**
 * Classe représentant une portion de trajet entre deux stations
 */
public record Section(Station start, Station arrival, double distance, int duration) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Crée une section
     *
     * @param start    la station de départ
     * @param arrival  la station d'arrivée
     * @param distance la longueur section
     * @param duration la durée en seconde de la section
     * @throws IllegalArgumentException si start ou arrival est `null`
     */
    public Section {
        if (start == null || arrival == null)
            throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return String.format("%s --> %s (%f, %d)", start.name(), arrival.name(), distance, duration);
    }
}
