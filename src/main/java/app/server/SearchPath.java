package app.server;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntBiFunction;
import app.map.Plan;
import app.map.Section;
import app.map.Station;
import app.map.Time;
import app.server.Dijkstra.PathNotFoundException;
import app.server.data.ErrorServer;
import app.server.data.Route;

/*
 * Calcule un trajet optimisé en distance ou en temps entre 2 stations et renvoie la liste des
 * sections du trajet
 */
public class SearchPath implements ServerActionCallback {
    private final Plan map;
    private final String start;
    private final String arrival;
    private final Time depart;
    private final ToIntBiFunction<Section, Section> getWeight;

    public SearchPath(Plan map, String start, String arrival, Time depart, boolean distance)
            throws IllegalArgumentException {
        if (map == null || start == null || arrival == null)
            throw new IllegalArgumentException();
        this.map = map;
        this.start = start;
        this.arrival = arrival;
        this.depart = depart;
        getWeight = distance ? Section::distanceTo : Section::durationTo;
    }

    /**
     * Calcule un trajet optimisé en distance ou en temps entre 2 stations et renvoie la liste des
     * sections du trajet
     */
    public Serializable execute() {
        try {
            List<Section> sections = new Dijkstra(map, start, arrival, depart, getWeight).getPath();
            return sectionsToRoute(sections);
        } catch (PathNotFoundException e) {
            return new ErrorServer("Trajet inexistant");
        }
    }

    private Route sectionsToRoute(List<Section> sections) {
        if (sections == null || sections.isEmpty())
            return new Route(sections);

        List<Section> route = new LinkedList<>();
        Section first = sections.get(0);
        Station start = first.getStart();
        Station arrival = first.getArrival();
        String line = map.getLineName(first);
        Time time = first.getTime();
        int distance = first.getDistance();
        int duration = first.getDuration();

        for (Section s : sections) {
            if (line.equals(map.getLineName(s))) {
                arrival = s.getArrival();
                distance += s.getDistance();
                duration += s.getDuration();
            } else {
                Section toAdd = new Section(start, arrival, line, distance, duration);
                toAdd.setTime(time);
                route.add(toAdd);
                start = s.getStart();
                arrival = s.getArrival();
                line = map.getLineName(s);
                time = s.getTime();
                distance = s.getDistance();
                duration = s.getDuration();
            }
        }
        Section toAdd = new Section(start, arrival, line, distance, duration);
        toAdd.setTime(time);
        route.add(toAdd);
        return new Route(route);
    }
}
