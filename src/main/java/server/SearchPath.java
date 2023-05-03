package server;

import java.io.Serializable;
import java.util.List;

import server.Dijkstra.PathNotFoundException;
import server.data.ErrorServer;
import server.data.Route;
import server.map.Plan;
import server.map.Section;
import server.map.Time;

/*
 * Calcule un trajet optimisé en distance ou en temps entre 2 stations et renvoie la liste des
 * sections du trajet
 */
public class SearchPath implements ServerActionCallback {
    /**
     * Le plan où chercher le trajet
     */
    private final Plan map;
    /**
     * Le nom de la station de départ
     */
    private final String start;
    /**
     * Le nom de la station d'arrivé
     */
    private final String arrival;
    /**
     * L'horaire de départ
     */
    private final Time depart;
    /**
     * Si optimisation en distance
     */
    private final boolean distOpt;
    /**
     * S'il peut y avoir des sections à pied
     */
    private final boolean foot;

    /**
     * @param map le plan où chercher le trajet
     * @param start le nom de la station de départ
     * @param arrival le nom de la station d'arrivé
     * @param depart l'horaire de départ
     * @param distOpt si optimisation en distance
     * @param foot s'il peut y avoir des sections à pied
     * @throws IllegalArgumentException si {@code map}, {@code start} ou {@code arrival} est
     *         {@code null}
     */
    public SearchPath(Plan map, String start, String arrival, Time depart, boolean distOpt,
            boolean foot) throws IllegalArgumentException {
        if (map == null || start == null || arrival == null)
            throw new IllegalArgumentException();
        this.map = map;
        this.start = start;
        this.arrival = arrival;
        this.depart = depart;
        this.distOpt = distOpt;
        this.foot = foot;
    }

    @Override
    public Serializable execute() {
        try {
            List<Section> sections =
                    new Dijkstra(map, start, arrival, depart, distOpt, foot).getPath();
            return sectionsToRoute(sections);
        } catch (PathNotFoundException e) {
            return new ErrorServer("Trajet inexistant");
        }
    }

    /**
     * Retire le variant de la ligne de chaque section dans {@code sections}
     *
     * @param sections une liste de sections
     * @return le résultat à envoyer au client
     */
    private Route sectionsToRoute(List<Section> sections) {
        if (sections == null || sections.isEmpty())
            return new Route(sections);
        sections.stream().forEach(map::setLineName);
        return new Route(sections);
    }
}
