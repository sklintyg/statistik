package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.helper.DocumentHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Warehouse implements Iterable<Aisle> {

    private final Map<String, Aisle> aisles = new HashMap<>();

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
}
