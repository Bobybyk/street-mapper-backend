package app.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import app.map.Line;
import app.map.Plan;
import app.map.Section;
import app.map.Time;
import app.server.data.DepartureTimes;
import app.server.data.StationTime;

public class SearchTime implements ServerActionCallback {
    private final Plan map;
    private final String station;
    private final Time time;
    private static final int LIMIT = 20;

    public SearchTime(Plan map, String station, Time time) {
        if (map == null || station == null || time == null)
            throw new IllegalArgumentException();
        this.map = map;
        this.station = station;
        this.time = time;
    }

    public DepartureTimes execute() {
        List<StationTime> allTime = departuresFromStation();
        allTime.sort(Comparator.comparing(StationTime::getTime, Time::compareTo));
        List<StationTime> afterTime = new ArrayList<>(allTime.stream()
                .filter(s -> time.compareTo(s.getTime()) < 0).limit(LIMIT).toList());
        int nextDay = LIMIT - afterTime.size();
        afterTime.addAll(allTime.stream().limit(nextDay).toList());
        return new DepartureTimes(afterTime);
    }

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
