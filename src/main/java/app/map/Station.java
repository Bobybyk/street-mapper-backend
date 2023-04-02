package app.map;

/**
 * Classe représentant une station, avec un nom et ses coordonnées
 */
public record Station(
        /**
         * Le nom de la station
         */
        String name,
        /**
         * La coordonnée en x
         */
        double coordinateX,
        /**
         * La coordonnée en y
         */
        double coordinateY) {

    @Override
    public String toString() {
        return String.format("%s (%f, %f)", name, coordinateX, coordinateY);
    }
}
