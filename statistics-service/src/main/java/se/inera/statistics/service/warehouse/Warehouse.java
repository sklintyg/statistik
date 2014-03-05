package se.inera.statistics.service.warehouse;

import java.util.Iterator;

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
        return new Iterator<WideLine>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < lines.length;
            }
            @Override
            public WideLine next() {
                return lines[index++];
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int getSize() {
        return lines.length;
    }
}
