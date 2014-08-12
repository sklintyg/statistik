package se.inera.statistics.service.warehouse.query;

import se.inera.statistics.service.warehouse.Sjukfall;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Counter<T> implements Comparable<Counter> {

    private final T key;
    private int countFemale;
    private int countMale;

    public Counter(T key) {
        this.key = key;
    }

    public void increase(Sjukfall sjukfall) {
        if (sjukfall.getKon() == 0) {
            countFemale++;
        } else {
            countMale++;
        }
    }

    @Override
    public int compareTo(Counter other) {
        int count = countFemale + countMale;
        int otherCount = other.countFemale + countMale;
        if (count > otherCount) {
            return -1;
        } else if (count == otherCount) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getCount() {
        return countFemale + countMale;
    }
    public int getCountFemale() {
        return countFemale;
    }
    public int getCountMale() {
        return countMale;
    }

    @Override
    public String toString() {
        return key + ":" + countFemale + ":" + countMale;
    }

    public T getKey() {
        return key;
    }

    public static <K> Map<K, Counter<K>> mapFor(Iterable<K> i) {
        Map<K, Counter<K>> counters = new HashMap<>();
        for (K next: i) {
            counters.put(next, new Counter(next));
        }
        return counters;
    }
}
