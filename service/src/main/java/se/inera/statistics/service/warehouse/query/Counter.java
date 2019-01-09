/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse.query;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.Sjukfall;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Counter<T> {

    private final T key;
    private int countFemale;
    private int countMale;

    public Counter(T key) {
        this.key = key;
    }

    public void increase(Sjukfall sjukfall) {
        if (sjukfall.getKon() == Kon.FEMALE) {
            countFemale++;
        } else {
            countMale++;
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

    public static Comparator<Counter> byTotalCount() {
        return (o1, o2) -> {
            int count = o1.countFemale + o1.countMale;
            int otherCount = o2.countFemale + o2.countMale;
            if (count > otherCount) {
                return -1;
            } else if (count == otherCount) {
                return 0;
            } else {
                return 1;
            }
        };
    }


}
