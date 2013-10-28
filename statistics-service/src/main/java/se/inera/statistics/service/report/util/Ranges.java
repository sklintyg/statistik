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

    public Ranges(Range...ranges) {
        this.ranges = Arrays.asList(ranges);
        for (Range range: this.ranges) {
            idMap.put(range.getName(), range);
        }
    }

    public static Range range(String name, int cutoff) {
        return new Range(name, cutoff);
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
        for (Range range: ranges) {
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
    public  List<Range> lookupRangesLongerThan(int days) {
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

    public static final class Range {
        private final String name;
        private final int cutoff;

        private Range(String name, int cutoff) {
            this.name = name;
            this.cutoff = cutoff;
        }

        public String getName() {
            return name;
        }

        public int getCutoff() {
            return cutoff;
        }
    }

}
