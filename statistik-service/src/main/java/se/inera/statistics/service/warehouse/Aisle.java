package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Aisle implements Iterable<Fact> {
    private final List<Fact> lines = new ArrayList<>();
    private static final Comparator<Fact> TIME_ORDER = new Comparator<Fact>() {
        @Override
        public int compare(Fact f1, Fact f2) {
            return f1.getStartdatum() < f2.getStartdatum() ? -1 : f1.getStartdatum() > f2.getStartdatum() ? 1 : 0;
        }
    };

    public void addLine(Fact line) {
        lines.add(line);
    }

    @Override
    public Iterator<Fact> iterator() {
        return lines.iterator();
    }

    public int getSize() {
        return lines.size();
    }

    public void sort() {
        Collections.sort(lines, TIME_ORDER);
    }

    List<Fact> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
