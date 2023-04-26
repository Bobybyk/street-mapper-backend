package app.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;
import app.map.PlanParser.UndefinedLineException;

public final class Plan {
    /**
     * Map où chaque nom de station est associé aux sections dont le départ est cette station
     */
    private final Map<String, List<Section>> map;

    /**
     * Map où chaque nom (avec variant) de ligne est associée sa ligne
     */
    private final Map<String, Line> lines;

    /**
     * Map ou le nom de la station est associé à ses informations
     */
    private final Map<String, StationInfo> stations;

    public Plan() {
        map = new HashMap<>();
        lines = new HashMap<>();
        stations = new HashMap<>();
    }

    /**
     * Copie les données des maps.
     *
     * @param p un plan à copier
     */
    public Plan(Plan p) {
        this.map = p.map.entrySet().stream().reduce(new HashMap<>(), (acc, e) -> {
            acc.put(e.getKey(), e.getValue().stream().map(Section::new).toList());
            return acc;
        }, (acc, m) -> {
            acc.putAll(m);
            return acc;
        });
        this.lines = new HashMap<>(p.lines);
        this.stations = new HashMap<>(p.stations);
    }

    /**
     * Ajoute les informations d'une section dans le plan
     *
     * @param startName le nom de la station de départ
     * @param startCoord les coordonnées de la station de départ
     * @param arrivalName le nom de la station d'arrivé
     * @param arrivalCoord les coordonnées de la station d'arrivé
     * @param lineName le nom de la ligne (avec son variant)
     * @param duration la durée de la section
     * @param distance la longueur de la section
     * @throws IndexOutOfBoundsException si l'une des coordonnées n'est pas bien formée
     */
    void addSection(String startName, double[] startCoord, String arrivalName,
            double[] arrivalCoord, String lineName, int[] duration, double distance)
            throws IndexOutOfBoundsException {
        Station start = addStation(startName, startCoord[0], startCoord[1]);
        Station arrival = addStation(arrivalName, arrivalCoord[0], arrivalCoord[1]);
        int durationMin = duration[0] * 60 + duration[1];
        int distanceMetre = (int) Math.round(distance * 1000);
        Line line = addSection(start, arrival, lineName, distanceMetre, durationMin);
        addStationInfo(startName, line.getName());
        addStationInfo(arrivalName, line.getName());
    }

    /**
     * Ajoute une station au plan
     *
     * @param name le nom de la station
     * @param latitude la coordonnée en latitude en degrés décimaux de la station
     * @param longitude la coordonnée en longitude en degrés décimaux de la station
     * @return la station créée
     */
    private Station addStation(String name, double latitude, double longitude) {
        Station station = new Station(name, latitude, longitude);
        map.putIfAbsent(name, new ArrayList<>());
        return station;
    }

    /**
     * Ajoute une section au plan
     *
     * @param start la station de départ de la section
     * @param arrival la station d'arrivé de la section
     * @param lineName le nom de la ligne (avec son variant) de la section
     * @param distance la longueur de la section
     * @param duration la durée de la section
     * @return la ligne de la section
     * @throws IndexOutOfBoundsException si le nom de la ligne n'est pas bien formé
     */
    private Line addSection(Station start, Station arrival, String lineName, int distance,
            int duration) throws IndexOutOfBoundsException {
        Section section = new Section(start, arrival, lineName, distance, duration);
        map.get(start.getName()).add(section);
        Line line = lines.computeIfAbsent(lineName, n -> {
            String[] lineVariant = n.split(" ");
            String name = lineVariant[0];
            String variant = lineVariant[2];
            return new Line(name, variant);
        });
        line.addSection(section);
        return line;
    }

    /**
     * Ajoute un nom de ligne aux informations d'une station dans le plan
     *
     * @param stationName le nom de la station
     * @param lineName le nom de la ligne
     */
    private void addStationInfo(String stationName, String lineName) {
        StationInfo info = stations.computeIfAbsent(stationName, StationInfo::new);
        info.addLine(lineName);
    }

    /**
     * Ajoute un horaire de départ d'une ligne au plan
     *
     * @param line le nom de la ligne (avec variant)
     * @param stationName le nom de la station de départ
     * @param time l'horaire de départ
     * @throws IndexOutOfBoundsException si l'horaire n'est pas bien formé
     * @throws UndefinedLineException si la ligne n'existe pas dans le plan
     * @throws StartStationNotFoundException si la station de départ n'existe pas sur la ligne
     * @throws DifferentStartException s'il y a plusieurs stations de départ pour une même ligne
     */
    void addDepartureTime(String line, String stationName, int[] time)
            throws IndexOutOfBoundsException, UndefinedLineException, StartStationNotFoundException,
            DifferentStartException, IllegalArgumentException {
        Line l = lines.get(line);
        if (l == null)
            throw new UndefinedLineException(line);
        l.setStart(stationName);
        l.addDepartureTime(time[0], time[1]);
    }

    public Map<String, List<Section>> getMap() {
        return new HashMap<>(map);
    }

    public Map<String, Line> getLines() {
        return new HashMap<>(lines);
    }

    public Set<StationInfo> getStationsInfo() {
        return new HashSet<>(stations.values());
    }

    public static class PathNotFoundException extends Exception {
        public PathNotFoundException(String start, String arrival) {
            super(String.format("Pas de chemin trouvé entre %s et %s", start, arrival));
        }

        public PathNotFoundException() {
            super();
        }
    }

    /**
     * Calcule un trajet entre 2 stations et renvoie la liste des sections du trajet
     *
     * @param startStation le nom de la station de départ
     * @param arrivalStation le nom de la station d'arrivé
     * @return la liste des sections du trajet
     * @throws IllegalArgumentException si start ou arrival est `null`
     * @throws PathNotFoundException si il n'existe pas de trajet entre les deux stations
     */
    public List<Section> findPathDistOpt(String startStation, String arrivalStation)
            throws IllegalArgumentException, PathNotFoundException {
        if (startStation == null || arrivalStation == null)
            throw new IllegalArgumentException();
        List<Section> sections =
                new Dijkstra(new Plan(this), startStation, arrivalStation, Section::distanceTo)
                        .getPath();
        return sectionsToRoute(sections);
    }

    private List<Section> sectionsToRoute(List<Section> sections) {
        if (sections == null || sections.isEmpty())
            return sections;

        List<Section> route = new LinkedList<>();
        Section first = sections.get(0);
        Station start = first.getStart();
        Station arrival = first.getArrival();
        String line = lines.get(first.getLine()).getName();
        Time time = first.getTime();
        int distance = first.getDistance();
        int duration = first.getDuration();

        for (Section s : sections) {
            if (line.equals(lines.get(s.getLine()).getName())) {
                arrival = s.getArrival();
                distance += s.getDistance();
                duration += s.getDuration();
            } else {
                Section toAdd = new Section(start, arrival, line, distance, duration);
                toAdd.setTime(time);
                route.add(toAdd);
                start = s.getStart();
                arrival = s.getArrival();
                line = lines.get(s.getLine()).getName();
                time = s.getTime();
                distance = s.getDistance();
                duration = s.getDuration();
            }
        }
        Section toAdd = new Section(start, arrival, line, distance, duration);
        toAdd.setTime(time);
        route.add(toAdd);
        return route;
    }
}
