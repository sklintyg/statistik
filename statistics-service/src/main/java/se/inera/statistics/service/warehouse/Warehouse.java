package se.inera.statistics.service.warehouse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Warehouse implements Iterable<Aisle> {

    private final Map<String, Aisle> aisles = new HashMap<>();
    private static IdMap<String> enhetsMap = new IdMap<>();

    public void accept(Fact fact, String vardgivareId) {
        Aisle aisle = getAisle(vardgivareId);
        aisle.addLine(fact);
    }

    private Aisle getAisle(String vardgivareId) {
        Aisle aisle = aisles.get(vardgivareId);
        if (aisle == null) {
            aisle = new Aisle();
            aisles.put(vardgivareId, aisle);
        }
        return aisle;
    }

    public Aisle get(String vardgivarId) {
        return getAisle(vardgivarId);
    }

    public String toString() {
        long total = 0;
        for (Aisle aisle: aisles.values()) {
            total += aisle.getSize();
        }
        return "Warehouse [" + aisles.size() + " aisles, " + total + " lines]";
    }

    public Map<String, Aisle> getAllVardgivare() {
        return Collections.unmodifiableMap(aisles);
    }

    public void clear() {
        aisles.clear();
    }

    @Override
    public Iterator<Aisle> iterator() {
        return aisles.values().iterator();
    }

    public static int getEnhetAndRemember(String id) {
        return enhetsMap.getId(id);
    }

    public static int getEnhet(String id) {
        return enhetsMap.getId(id);
    }

    private static class IdMap<T> {
        private final Map<T, Integer> map = new HashMap<>();

        public synchronized Integer getId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                id = map.size() + 1;
                map.put(key, id);
            }
            return id;
        }

        public synchronized Integer maybeGetId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                return -1;
            } else {
                return id;
            }
        }
    }

}
