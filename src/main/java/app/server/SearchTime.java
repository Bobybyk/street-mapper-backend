package app.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import app.map.Line;
import app.map.Plan;
import app.map.Section;
import app.map.Station;
import app.map.Time;

public class SearchTime implements ServerActionCallback {
    private final Plan map;
    private final String station;
    private final Time time;

    public SearchTime(Plan map, String station, Time time) {
        if (map == null || station == null || time == null)
            throw new IllegalArgumentException();
        this.map = map;
        this.station = station;
        this.time = time;
    }

    public Serializable execute() {
        return null;
    }

    public HashMap<String, LinkedList<Time>> departuresFromStation(Station station) {
        List<Section> sectionsFromStation = map.getMap().get(station.getName());
        HashMap<String, LinkedList<Section>> sectionTable = new HashMap<>();
        for (Section s : sectionsFromStation) {
            Line line = map.getLines().get(s.getLine());
            sectionTable.putIfAbsent(line.getName() + " variant " + line.getVariant(),
                    new LinkedList<>());
        }
        for (Section s : sectionsFromStation) {
            Line line = map.getLines().get(s.getLine());
            sectionTable.get(line.getName() + " variant " + line.getVariant()).addLast(s);
        }
        HashMap<String, LinkedList<Time>> timeTable = new HashMap<>();
        for (Section s : sectionsFromStation) {
            Line line = map.getLines().get(s.getLine());
            timeTable.putIfAbsent(line.getName() + " variant " + line.getVariant(),
                    line.getDepartureTimeFromStation(s));
        }
        return timeTable;
    }

    public HashMap<String, LinkedList<Time>> departuresFromStationFromTime(Station station,
            Time start) {
        HashMap<String, LinkedList<Time>> timeTable = departuresFromStation(station);
        HashMap<String, LinkedList<Time>> timeTableFiltered = new HashMap<>();
        for (String s : timeTable.keySet()) {
            LinkedList<Time> timeList = new LinkedList<>();
            for (Time t : timeTable.get(s)) {
                if (start.compareTo(t) < 0)
                    timeList.addLast(t);
            }
            timeTableFiltered.put(s, timeList);
        }
        return timeTableFiltered;
    }
}
