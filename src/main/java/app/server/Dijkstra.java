package app.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.ToIntBiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import app.map.Plan;
import app.map.Section;
import app.map.Station;
import app.map.Time;
import app.util.Parser;

/**
 * Implémentation de l'algorithme de Dijkstra sur un plan
 */
public final class Dijkstra {
    /**
     * Le plan du réseau
     */
    private final Plan plan;
    /**
     * Le graphe : les sommets sont les noms des stations et les arêtes sont les sections
     */
    private final Map<String, List<Section>> map;
    /**
     * Le sommet de départ
     */
    private final String start;
    /**
     * Le sommet d'arrivé
     */
    private final String arrival;
    /**
     * L'horaire de départ
     */
    private final Time departTime;
    /**
     * Optimisation en distance ou en temps
     */
    private boolean distOpt;
    /**
     * Si des sections à pied sont possibles
     */
    private boolean foot;
    /**
     * La distance maximale à parcourir à pied entre 2 sections
     */
    private static final int MAX_FOOT_DISTANCE = 1000;
    /**
     * Le poids pour les trajets à pied
     */
    private static final double WEIGHT_FOOT = 1.5;
    /**
     * La fonction qui calcule le poids d'une arrête à partir de la dernière arête traitée
     */
    private final ToIntBiFunction<Section, Section> getWeight;
    /**
     * Associe chaque sommet à sa distance par rapport au sommet de départ
     */
    private final Map<String, Integer> distance;
    /**
     * Associe chaque sommet à l'arête pris pour arriver à ce sommet
     */
    private final Map<String, Section> previous;
    /**
     * File de priorité sur les sommets par rapport à leur distance avec le sommet de départ
     */
    private final PriorityQueue<String> queue;
    /**
     * Le sommet en cours de traitement
     */
    private String u;
    /**
     * Le résultat de l'algorithme
     */
    private List<Section> result;
    private static final String DEPART = "Départ";
    private static final String ARRIVEE = "Arrivée";

    /**
     * @param plan le plan à utiliser
     * @param start le sommet de départ
     * @param arrival le sommet d'arrivé
     * @param departTime l'horaire de départ
     * @param getWeight la fonction qui associe à une section (arête) son poids
     */
    Dijkstra(Plan plan, String start, String arrival, Time departTime, boolean distOpt,
            boolean foot) {
        if (plan == null || start == null || arrival == null)
            throw new IllegalArgumentException();
        this.plan = plan;
        this.map = plan.getMap();
        this.start = initStart(start);
        this.arrival = initArrival(arrival);
        this.departTime = departTime;
        this.distOpt = distOpt;
        this.foot = foot;
        this.getWeight = distOpt ? Section::distanceTo : Section::durationTo;
        distance = new HashMap<>();
        previous = new HashMap<>();
        queue = new PriorityQueue<>(map.size(), Comparator.comparingInt(distance::get));
        this.u = null;
        this.result = null;
    }

    /**
     * Ajoute si nécessaire des arêtes dans {@code map} permettant de relié une coordonnée aux
     * stations du réseau.
     *
     * @param start un nom de station ou une coordonnée
     * @return {@code DEPART} s'il s'agit d'une coordonnée ou le nom de la station sinon
     */
    private String initStart(String start) {
        Pattern p = Pattern.compile("^\\((.*)\\)$");
        Matcher m = p.matcher(start);
        if (m.matches()) {
            try {
                double[] coord = Parser.parse2DoubleSep(m.group(1), ",");
                Station s = new Station(DEPART, coord[0], coord[1]);
                List<Section> sections = getSectionsFromStations(true, s, getCloseStations(s));
                if (sections.isEmpty()) {
                    Station closest = getClosestStation(s);
                    if (closest != null)
                        sections.add(new Section(s, closest, null, s.distanceBetween(closest),
                                s.durationBetween(closest)));
                }
                map.put(DEPART, sections);
                return DEPART;
            } catch (Exception ignored) {
            }
        }
        return start;
    }

    /**
     * Ajoute si nécessaire des arêtes dans {@code map} permettant de relié une coordonnée aux
     * stations du réseau.
     *
     * @param arrival un nom de station ou une coordonnée
     * @return {@code ARRIVEE} s'il s'agit d'une coordonnée ou le nom de la station sinon
     */
    private String initArrival(String arrival) {
        Pattern p = Pattern.compile("^\\((.*)\\)$");
        Matcher m = p.matcher(arrival);
        if (m.matches()) {
            try {
                double[] coord = Parser.parse2DoubleSep(m.group(1), ",");
                Station s = new Station(ARRIVEE, coord[0], coord[1]);
                List<Section> sections = getSectionsFromStations(false, s, getCloseStations(s));
                if (sections.isEmpty()) {
                    Station closest = getClosestStation(s);
                    if (closest != null)
                        sections.add(new Section(closest, s, null, closest.distanceBetween(s),
                                closest.durationBetween(s)));
                }
                for (Section toArrival : sections)
                    map.get(toArrival.getStart().getName()).add(toArrival);
                return ARRIVEE;
            } catch (Exception ignored) {
            }
        }
        return arrival;
    }

    /**
     * Aucun chemin n'a été trouvé
     */
    public static class PathNotFoundException extends Exception {
        public PathNotFoundException(String start, String arrival) {
            super(String.format("Pas de chemin trouvé entre %s et %s", start, arrival));
        }

        public PathNotFoundException() {
            super();
        }
    }

    /**
     * Recherche un chemin entre 2 sommets en appliquant l'algorithme de dijkstra et renvoie la
     * liste des arêtes dans l'ordre du chemin
     *
     * @return la liste des arêtes dans l'ordre du départ à l'arrivé
     * @throws PathNotFoundException s'il n'existe pas de chemin entre les deux sommets
     */
    List<Section> getPath() throws PathNotFoundException {
        if (result == null)
            compute();
        return result;
    }

    /**
     * Exécute l'algorithme de dijkstra
     *
     * @throws PathNotFoundException s'il n'existe pas de chemin entre les deux sommets
     */
    private void compute() throws PathNotFoundException {
        init();
        while (!finished())
            loop();
        if (arrival.equals(u))
            setResult();
        else
            throw new PathNotFoundException();
    }

    /**
     * Initialise les valeurs pour l'algorithme
     */
    private void init() {
        for (String station : map.keySet()) {
            distance.put(station, Integer.MAX_VALUE);
        }
        distance.put(start, 0);
        distance.put(arrival, Integer.MAX_VALUE);
        queue.addAll(map.keySet());
    }

    /**
     * @return {@code true} si l'algo à terminer {@code false} sinon
     */
    private boolean finished() {
        if (queue.isEmpty())
            return true;
        u = queue.poll();
        return arrival.equals(u) || distance.get(u) == Integer.MAX_VALUE;
    }

    /**
     * @param station un sommet
     * @return une liste de sommet à moins de {@code MAX_FOOT_DISTANCE} mètres de {@code station}
     */
    private List<Station> getCloseStations(Station station) {
        return plan.getStations().stream()
                .filter(s -> !s.equals(station) && station.distanceBetween(s) < MAX_FOOT_DISTANCE)
                .toList();
    }

    /**
     * @param station un sommet
     * @return le sommet le plus près de {@code station}
     */
    private Station getClosestStation(Station station) {
        return plan.getStations().stream().sorted(Comparator.comparingInt(station::distanceBetween))
                .findFirst().orElse(null);
    }

    /**
     * @param from si {@code station} est le sommet de départ des arêtes
     * @param station le sommet de départ des arêtes
     * @param list une liste de sommets d'arrivé si {@code from}, sinon une liste de sommets de
     *        départ
     * @return la liste des arêtes à pied entre {@code station} chaque sommet de {@code list}
     */
    private List<Section> getSectionsFromStations(boolean from, Station station,
            List<Station> list) {
        List<Section> res = new ArrayList<>();
        for (Station s : list) {
            res.add(new Section(from ? station : s, from ? s : station, null,
                    station.distanceBetween(s), station.durationBetween(s)));
        }
        return res;
    }

    /**
     * @return la liste des arêtes partant de {@code u} en prenant en compte les sections à pied
     */
    private List<Section> getNeighbors() {
        Section prev = previous.get(u);
        List<Section> neighbors = map.get(u);
        if (neighbors == null)
            neighbors = new ArrayList<>();

        if (prev == null && !start.equals(DEPART) && foot) {
            Set<Station> startStations =
                    new HashSet<>(neighbors.stream().map(Section::getStart).toList());
            for (Station s : startStations) {
                neighbors.addAll(getSectionsFromStations(true, s, getCloseStations(s)));
            }
        } else if (prev != null && foot) {
            neighbors.addAll(getSectionsFromStations(true, prev.getArrival(),
                    getCloseStations(prev.getArrival())));
        }
        return neighbors;
    }

    /**
     * Corps de l'algorithme
     */
    private void loop() {
        for (Section section : getNeighbors()) {
            Section prev = previous.get(u);
            if (prev == null) {
                prev = new Section(section.getStart(), section.getStart(), "", 0, 0);
                prev.setTime(departTime);
            }
            plan.updateSectionTime(section, prev.getArrivalTime());
            if (distOpt || section.getTime() != null) {
                String v = section.getArrival().getName();
                int weight = getWeight.applyAsInt(prev, section);
                int w = distance.get(u)
                        + (section.getLine() == null ? (int) Math.round(weight * WEIGHT_FOOT)
                                : weight);
                if (distance.get(v) > w) {
                    distance.put(v, w);
                    previous.put(v, section);
                    queue.remove(v);
                    queue.add(v);
                }
            }
        }
    }

    /**
     * Met la liste des arêtes dans l'ordre du chemin dans {@code result}
     *
     * @throws PathNotFoundException s'il n'existe pas de chemin entre les deux sommets
     */
    private void setResult() throws PathNotFoundException {
        List<Section> orderedPath = new LinkedList<>();
        String last = arrival;
        while (!last.equals(start)) {
            Section section = previous.get(last);
            if (section == null)
                throw new PathNotFoundException();
            orderedPath.add(section);
            last = section.getStart().getName();
        }
        Collections.reverse(orderedPath);
        result = orderedPath;
    }
}
