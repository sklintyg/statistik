package se.inera.statistics.service.warehouse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Warehouse implements Iterable<Aisle> {

    private static final Logger LOG = LoggerFactory.getLogger(Warehouse.class);

    private volatile Map<String, Aisle> aisles = new HashMap<>();
    private Map<String, Aisle> loadingAisles = new HashMap<>();
    private static IdMap<String> enhetsMap = new IdMap<>();
    private static IdMap<String> lakareMap = new IdMap<>();
    private LocalDateTime lastUpdate = null;

    public void accept(Fact fact, String vardgivareId) {
        Aisle aisle = getAisle(vardgivareId, loadingAisles, true);
        aisle.addLine(fact);
    }

    public Aisle get(String vardgivarId) {
        return getAisle(vardgivarId, aisles, false);
    }

    private Aisle getAisle(String vardgivareId, Map<String, Aisle> aisles, boolean add) {
        Aisle aisle = aisles.get(vardgivareId);
        if (aisle == null) {
            aisle = new Aisle();
            if (add) {
                aisles.put(vardgivareId, aisle);
            }
        }
        return aisle;
    }

    public String toString() {
        long total = 0;
        for (Aisle aisle: aisles.values()) {
            total += aisle.getSize();
        }
        return "Warehouse [" + aisles.size() + " aisles, " + total + " lines]";
    }

    public Map<String, Aisle> getAllVardgivare() {
        return aisles;
    }

    @Override
    public Iterator<Aisle> iterator() {
        return aisles.values().iterator();
    }

    public static int getEnhetAndRemember(String id) {
        return enhetsMap.getOrCreateId(id);
    }

    public static int getEnhet(String id) {
        return enhetsMap.maybeGetId(id);
    }

    public static int getNumLakarIdAndRemember(String id) {
        return lakareMap.getOrCreateId(id);
    }

    public static int getNumLakarId(String id) {
        return lakareMap.maybeGetId(id);
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void complete(LocalDateTime lastUpdate) {
        for (Aisle aisle: loadingAisles.values()) {
            aisle.sort();
        }
        aisles = Collections.unmodifiableMap(loadingAisles);
        this.lastUpdate = lastUpdate;
        loadingAisles = new HashMap<>();
    }

    public void clear(){
        LOG.warn("Clearing warehouse ailes");
        loadingAisles.clear();
        complete(LocalDateTime.now());
    }

    private static class IdMap<T> {
        private final BiMap<T, Integer> map = HashBiMap.create();

        public synchronized Integer getOrCreateId(T key) {
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
