/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse.query;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.Sjukfall;

import java.util.HashMap;
import java.util.Map;

public class Counter<T> implements Comparable<Counter> {

    private final T key;
    private int countFemale;
    private int countMale;

    public Counter(T key) {
        this.key = key;
    }

    public void increase(Sjukfall sjukfall) {
        if (sjukfall.getKon() == Kon.Female) {
            countFemale++;
        } else {
            countMale++;
        }
    }

    @Override
    public int compareTo(Counter other) {
        int count = countFemale + countMale;
        int otherCount = other.countFemale + other.countMale;
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
