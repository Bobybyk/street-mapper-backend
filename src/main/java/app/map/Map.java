package app.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Classe représentant la carte
 */
public final class Map {

    public class IncorrectFileFormatException extends Exception {
        public IncorrectFileFormatException(String filename) {
            super(String.format("Le fichier %s n'est pas bien formé", filename));
        }
    }

    public class UndefinedStation extends Exception {
        public UndefinedStation(String station) {
            super(String.format("La station %s n'existe pas dans la carte", station));
        }
    }

    public class UndefinedTime extends Exception {
        public UndefinedTime(String line) {
            super(String.format("Il y a des sections sans horaires sur la ligne %s", line));
        }
    }

    /**
     * Map où chaque station est associée aux sections dont le départ est cette
     * station
     */
    private final HashMap<Station, ArrayList<Section>> map = new HashMap<>();
    /**
     * Map où chaque ligne est associée aux sections de la ligne
     */
    private final HashMap<String, ArrayList<Section>> lines = new HashMap<>();

    /**
     * 
     * @param mapFileName
     * @param timeFileName
     * @throws IncorrectFileFormatException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    public Map(String mapFileName, String timeFileName)
            throws IncorrectFileFormatException, FileNotFoundException, IllegalArgumentException, UndefinedStation,
            UndefinedTime {
        if (mapFileName == null || timeFileName == null)
            throw new IllegalArgumentException();
        parseMap(mapFileName);
        parseTime(timeFileName);
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

    private void addSection(Station start, Station arrival, double distance, int duration, String line) {
        Section section = new Section(start, arrival, distance, duration, line);
        // add to map
        ArrayList<Section> sections = map.getOrDefault(start, new ArrayList<>());
        sections.add(section);
        map.put(start, sections);
        // add to lines
        sections = lines.getOrDefault(line, new ArrayList<>());
        sections.add(section);
        lines.put(line, sections);
    }

    private void parseTime(String fileName)
            throws IncorrectFileFormatException, FileNotFoundException, UndefinedStation, UndefinedTime {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        /**
         * Map où chaque ligne est associée à la liste des sections partant d'un
         * terminus
         */
        HashMap<String, ArrayList<Section>> departTimes = new HashMap<>();
        try {
            while (sc.hasNextLine()) {
                handleTimeLine(departTimes, sc.nextLine());
            }
            timeSpread(departTimes);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IncorrectFileFormatException(file.getName());
        } finally {
            sc.close();
        }
    }

    private void handleTimeLine(HashMap<String, ArrayList<Section>> departTimes, String s) throws UndefinedStation {
        String[] data = s.split(";");
        String line = data[0].trim();
        String stationName = data[1].trim();
        String[] time = data[2].trim().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        addDepartureTime(departTimes, line, stationName, hour, minute);
    }

    private void addDepartureTime(HashMap<String, ArrayList<Section>> departTimes, String line, String stationName,
            int hour, int minute) throws UndefinedStation {
        // recherche les terminus de line qui ont déjà été vu
        ArrayList<Section> sections = departTimes.get(line);
        if (sections != null) {
            // recherche la section au départ du terminus stationName
            Optional<Section> terminus = sections.stream()
                    .filter(section -> section.getStartStationName().equals(stationName))
                    .findAny();
            if (terminus.isPresent()) {
                terminus.get().addTimes(hour, minute);
                return;
            }
        } // on a jamais vu line ou on a pas vu le terminus
          // on cherche les sections de line
        sections = lines.getOrDefault(line, new ArrayList<>());
        // on cherche la section partant de stationName
        Optional<Section> terminus = sections.stream()
                .filter(section -> section.getStartStationName().equals(stationName))
                .findAny();
        if (terminus.isPresent()) {
            terminus.get().addTimes(hour, minute);
        } else
            throw new UndefinedStation(stationName);
    }

    private void timeSpread(HashMap<String, ArrayList<Section>> departTimes) throws UndefinedTime {
        for (java.util.Map.Entry<String, ArrayList<Section>> entry : lines.entrySet()) {
            String line = entry.getKey();
            // liste des sections dont les horaires sont a définir
            ArrayList<Section> sections = new ArrayList<>(entry.getValue());
            // parcourir les terminus de la ligne
            for (Section terminus : departTimes.getOrDefault(line, new ArrayList<>())) {
                // retire la section du terminus dont les horaires ont été définies précédemment
                // par addDepartureTime
                sections.remove(terminus);
                Section current = terminus;
                while (!sections.isEmpty()) {
                    // recherche de la section suivante
                    Station nextDepart = current.getArrival();
                    Optional<Section> next = sections.stream().filter(s -> s.getStart().equals(nextDepart))
                            .findAny();
                    if (!next.isPresent()) {
                        throw new UndefinedTime(line);
                    }
                    int previousDuration = current.getDuration();
                    // calcule les horaires pour la section next
                    List<Time> times = current.getDepartures().stream().map(t -> t.addDuration(previousDuration))
                            .toList();
                    // ajoute les horaires à la section next
                    current = next.get();
                    current.addTimes(times);
                    sections.remove(current);
                }
            }
            if (!sections.isEmpty())
                throw new UndefinedTime(line);
        }
    }

}
