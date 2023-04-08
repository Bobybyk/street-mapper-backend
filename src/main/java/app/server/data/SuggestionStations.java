package app.server.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import app.map.LignedStation;


/**
 * Classe représentant l'ensemble des stations (station + ligne) coorespondant la demande du client
 * 
 */
public class SuggestionStations implements Serializable, Collection<LignedStation> {

    @Serial
    private static final long serialVersionUID = 1L;

    private SortedSet<LignedStation> stations;

    public SuggestionStations() {
        this.stations = new TreeSet<>(LignedStation::compareTo);
    }

    public void filterWithPrefix(String prefixStation) {
        SortedSet<LignedStation> filteredStation = new TreeSet<>(LignedStation::compareTo);
        filteredStation.addAll(
            this.stream().filter(lignedStation -> 
            lignedStation.getLine().startsWith(prefixStation)).collect(Collectors.toSet())
        );
        stations = filteredStation;
    }

    @Override
    public int size() {
        return stations.size();
    }

    @Override
    public boolean isEmpty() {
        return stations.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return stations.contains(o);
    }

    @Override
    public Iterator<LignedStation> iterator() {
        return stations.iterator();
    }

    @Override
    public Object[] toArray() {
        return stations.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return stations.toArray(a);
    }

    @Override
    public boolean add(LignedStation e) {
        return stations.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return stations.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return stations.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends LignedStation> c) {
        return stations.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return stations.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return stations.removeAll(c);
    }

    @Override
    public void clear() {
        stations.clear();
    }
}
