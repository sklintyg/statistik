/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class Ranges implements Iterable<Ranges.Range> {

    private final List<Range> ranges;
    private final Map<String, Range> idMap = new TreeMap<>();

    public Ranges(Range... ranges) {
        this.ranges = Arrays.asList(ranges);
        for (Range range : this.ranges) {
            idMap.put(range.getName(), range);
        }
    }

    public static Range range(String name, int cutoff) {
        return new Range(name, cutoff);
    }

    public static Range range(String name, int cutoff, String color) {
        return new Range(name, cutoff, color);
    }

    public Range rangeFor(String id) {
        Range range = idMap.get(id);
        if (range != null) {
            return range;
        } else {
            throw new IllegalStateException("Ranges have not been defined correctly. No range " + id);
        }
    }

    public Range rangeFor(int value) {
        for (Range range : ranges) {
            if (range.cutoff > value) {
                return range;
            }
        }
        throw new IllegalStateException("Ranges have not been defined correctly. No range includes " + value);
    }

    /**
     * Returns list of range where at least part of the range is higher than cutoff.
     *
     * @param days days
     * @return groups
     */
    public List<Range> lookupRangesLongerThan(int days) {
        List<Range> result = new ArrayList<>();
        for (Range range : ranges) {
            if (range.cutoff > days) {
                result.add(range);
            }
        }
        return result;
    }

    @Override
    public Iterator<Range> iterator() {
        return ranges.iterator();
    }

    public int getRangeCutoffForValue(int value) {
        for (Ranges.Range range : ranges) {
            final int cutoff = range.getCutoff();
            if (cutoff > value) {
                return cutoff;
            }
        }
        throw new IllegalStateException("Ranges have not been defined correctly. No range includes " + value);
    }

    public static final class Range {

        private final String name;
        private final int cutoff;
        private final String color;

        private Range(String name, int cutoff) {
            this(name, cutoff, null);
        }

        private Range(String name, int cutoff, String color) {
            this.name = name;
            this.cutoff = cutoff;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public int getCutoff() {
            return cutoff;
        }

        @Override
        public String toString() {
            return name + "(" + cutoff + ")";
        }
    }

}
