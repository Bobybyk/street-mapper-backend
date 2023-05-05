package server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import server.data.DepartureTimes;
import server.data.ServerResponse;
import server.data.StationTime;
import server.map.Line;
import server.map.Plan;
import server.map.Section;
import server.map.Time;

/**
 * Calcule les prochains horaires de passages à une certaine station
 */
public class SearchTime implements ServerActionCallback {
    /**
     * Le plan où chercher les horaires
     */
    private final Plan map;
    /**
     * La station où chercher les horaires
     */
    private final String station;
    /**
     * L'heure minimale
     */
    private final Time time;
    /**
     * Le nombre d'horaires du résultat
     */
    private static final int LIMIT = 20;

    /**
     * @param map le plan où chercher les horaires
     * @param station la station où chercher les horaires
     * @param time l'heure minimale
     * @throws IllegalArgumentException si {@code map}, {@code station} ou {@code time} est
     *         {@code null}
     */
    public SearchTime(Plan map, String station, Time time) throws IllegalArgumentException {
        if (map == null || station == null || time == null)
            throw new IllegalArgumentException();
        this.map = map;
        this.station = station;
        this.time = time;
    }

    @Override
    public ServerResponse execute() {
        List<StationTime> allTime = departuresFromStation();
        allTime.sort(Comparator.comparing(StationTime::getTime, Time::compareTo));
        List<StationTime> afterTime =
                new ArrayList<>(allTime.stream().filter(s -> time.compareTo(s.getTime()) < 0)
                        .limit(LIMIT).collect(Collectors.toCollection(ArrayList::new)));
        int nextDay = LIMIT - afterTime.size();
        afterTime.addAll(
                allTime.stream().limit(nextDay).collect(Collectors.toCollection(ArrayList::new)));
        return new DepartureTimes(afterTime);
    }

    /**
     * @return la liste des horaires de départ {@code station}
     */
    private List<StationTime> departuresFromStation() {
        List<StationTime> times = new ArrayList<>();
        List<Section> sectionsFromStation = map.getMap().get(station);
        if (sectionsFromStation != null) {
            for (Section s : sectionsFromStation) {
                Line l = map.getLine(s);
                if (l != null) {
                    l.getDepartureTime(s).forEach(t -> times.add(
                            new StationTime(l.getName(), l.getLast().getArrival().getName(), t)));
                }
            }
        }
        return times;
    }
}
