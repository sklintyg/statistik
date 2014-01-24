package se.inera.statistics.service.warehouse;

import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;

public class Warehouse implements Iterable<WideLine> {
    private final WideLine[] lines;

    public Warehouse(int size) {
        lines = new WideLine[size];
    }

    public WideLine[] getMatchingLines() {
        return null;
    }

    public void setLine(int i, WideLine line) {
        lines[i] = line;
    }

    @Override
    public Iterator<WideLine> iterator() {
        return new ArrayIterator(lines);
    }

    public int getSize() {
        return lines.length;
    }
}
