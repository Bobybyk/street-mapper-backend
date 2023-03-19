package app.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Classe représentant la carte
 */
public final class Map {

    public class IncorrectFileFormatException extends Exception {
        public IncorrectFileFormatException(String filename) {
            super(String.format("Le fichier %s n'est pas bien formé", filename));
        }
    }

    public class UndefinedLine extends Exception {
        public UndefinedLine(String line) {
            super(String.format("La line %s n'existe pas dans la carte", line));
        }
    }

    public static class StartStationNotFound extends Exception {
        public StartStationNotFound(String station, String line) {
            super(String.format("La station %s n'est pas sur la ligne %s", station, line));
        }
    }

    public static class DifferentStartException extends Exception {
        public DifferentStartException(String line) {
            super(String.format("Il y plusieurs stations de départ pour la ligne %s", line));
        }
    }

    /**
     * Map où chaque station est associée aux sections dont le départ est cette
     * station
     */
    private final HashMap<Station, ArrayList<Section>> map = new HashMap<>();
    /**
     * Map où chaque nom (avec variant) de ligne est associée sa ligne
     */
    private final HashMap<String, Line> lines = new HashMap<>();

    /**
     * 
     * @param mapFileName
     * @throws IncorrectFileFormatException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    public Map(String mapFileName)
            throws IncorrectFileFormatException, FileNotFoundException, IllegalArgumentException {
        if (mapFileName == null)
            throw new IllegalArgumentException();
        parseMap(mapFileName);
    }

    private void parseMap(String fileName) throws IncorrectFileFormatException, FileNotFoundException {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        try {
            while (sc.hasNextLine()) {
                handleMapLine(sc.nextLine());
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IncorrectFileFormatException(file.getName());
        } finally {
            sc.close();
        }
    }

    private void handleMapLine(String s) {
        String[] data = s.split(";");
        Station start = parseStation(data[0], data[1]);
        Station arrival = parseStation(data[2], data[3]);
        String line = data[4].trim();
        String[] time = data[5].trim().split(":");
        // on suppose que la durée est donnée au format mm:ss
        int duration = Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
        double distance = Double.parseDouble(data[6].trim());
        addSection(start, arrival, distance, duration, line);
    }

    private Station parseStation(String station, String coord) {
        station = station.trim();
        String[] coords = coord.trim().split(",");
        double x = Double.parseDouble(coords[0]);
        double y = Double.parseDouble(coords[1]);
        return new Station(station, x, y);
    }

    private void addSection(Station start, Station arrival, double distance, int duration, String lineName) {
        // création de la section
        String[] lineVariant = lineName.split(" ");
        String name = lineVariant[0];
        int variant = Integer.parseInt(lineVariant[2]);
        Section section = new Section(start, arrival, distance, duration);
        // ajout dans map
        ArrayList<Section> sections = map.getOrDefault(start, new ArrayList<>());
        sections.add(section);
        // ajout dans lines
        Line line = lines.getOrDefault(name, new Line(name, variant));
        line.addSection(section);
    }

    public void addTime(String fileName)
            throws IncorrectFileFormatException, FileNotFoundException, UndefinedLine, StartStationNotFound,
            DifferentStartException {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        try {
            while (sc.hasNextLine()) {
                handleTimeLine(sc.nextLine());
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IncorrectFileFormatException(file.getName());
        } finally {
            sc.close();
        }
    }

    private void handleTimeLine(String s) throws UndefinedLine, StartStationNotFound, DifferentStartException {
        String[] data = s.split(";");
        String line = data[0].trim();
        String stationName = data[1].trim();
        String[] time = data[2].trim().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        addDepartureTime(line, stationName, hour, minute);
    }

    private void addDepartureTime(String line, String stationName, int hour, int minute)
            throws UndefinedLine, StartStationNotFound, DifferentStartException {
        Line l = lines.get(line);
        if (l == null)
            throw new UndefinedLine(line);
        if (l.getStart() == null)
            try {
                l.setStart(stationName);
            } catch (IllegalArgumentException e) {
                throw new StartStationNotFound(line, stationName);
            }
        else if (!l.isStartingAt(stationName))
            throw new DifferentStartException(line);
        l.addDepartureTime(hour, minute);
    }

    public class PathNotFoundException extends Exception {
        public PathNotFoundException(Station start, Station arrival) {
            super(String.format("Pas de chemin trouvé entre %s et %s", start.getName(), arrival.getName()));
        }
    }

    /**
     * Calcule un trajet entre 2 stations et renvoie la liste des sections du trajet
     *
     * @param start   la station de départ
     * @param arrival la station d'arrivée
     * @return la liste des sections du trajet
     * @throws PathNotFoundException si il n'existe pas de trajet les deux stations
     */
    public ArrayList<Section> findPathDistOpt(Station start, Station arrival) throws PathNotFoundException {
        HashMap<Station, Section> path = dijkstra(start, arrival, Section::getDistance);
        ArrayList<Section> orderedPath = new ArrayList<>();
        Station previous = arrival;
        while (previous != start) {
            Section section = path.get(previous);
            orderedPath.add(section);
            previous = section.getStart();
        }
        Collections.reverse(orderedPath);
        return orderedPath;
    }

    /**
     * Recherche un chemin entre 2 stations en appliquant l'algorithme de
     * dijkstra et renvoie les sections du trajet
     *
     * @param start   la station (sommet) de départ
     * @param arrival la station (sommet) d'arrivée
     * @param f       la fonction qui associe à une section (arête) son poids
     * @return map associant une station à la section prendre pour aller à cette
     *         station
     * @throws PathNotFoundException si il n'existe pas de trajet les deux stations
     */
    private HashMap<Station, Section> dijkstra(Station start, Station arrival, Function<Section, Double> f)
            throws PathNotFoundException {
        HashMap<Station, Double> distance = new HashMap<>();
        HashMap<Station, Section> previous = new HashMap<>();
        for (java.util.Map.Entry<Station, ArrayList<Section>> entry : map.entrySet()) {
            Station station = entry.getKey();
            distance.put(station, Double.MAX_VALUE);
            previous.put(station, null);
        }

        distance.put(start, 0.);
        PriorityQueue<Station> queue = new PriorityQueue<>(map.size(),
                Comparator.comparingDouble(x -> distance.get(x)));
        queue.addAll(map.keySet());

        Station u = null;
        while (!queue.isEmpty() && (u = queue.poll()) != arrival) {
            for (Section section : map.get(u)) {
                Station v = section.getArrival();
                double w = distance.get(u) + f.apply(section);
                if (distance.get(v) > w) {
                    distance.put(v, w);
                    previous.put(v, section);
                    queue.remove(v);
                    queue.add(v);
                }
            }
        }
        if (queue.isEmpty())
            throw new PathNotFoundException(start, arrival);
        return previous;
    }
}
