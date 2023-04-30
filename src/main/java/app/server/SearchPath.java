package app.server;

import java.io.Serializable;
import java.util.List;
import app.map.Plan;
import app.map.Section;
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
    private final boolean distOpt;

    public SearchPath(Plan map, String start, String arrival, Time depart, boolean distOpt)
            throws IllegalArgumentException {
        if (map == null || start == null || arrival == null)
            throw new IllegalArgumentException();
        this.map = map;
        this.start = start;
        this.arrival = arrival;
        this.depart = depart;
        this.distOpt = distOpt;
    }

    /**
     * Calcule un trajet optimisé en distance ou en temps entre 2 stations et renvoie la liste des
     * sections du trajet
     */
    public Serializable execute() {
        try {
            List<Section> sections = new Dijkstra(map, start, arrival, depart, distOpt).getPath();
            return sectionsToRoute(sections);
        } catch (PathNotFoundException e) {
            return new ErrorServer("Trajet inexistant");
        }
    }

    private Route sectionsToRoute(List<Section> sections) {
        if (sections == null || sections.isEmpty())
            return new Route(sections);
        return new Route(sections.stream().map(s -> s.changeLine(map.getLineName(s))).toList());
    }
}
