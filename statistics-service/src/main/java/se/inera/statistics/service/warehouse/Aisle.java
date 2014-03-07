package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Aisle implements Iterable<Fact> {
    private final List<Fact> lines = new ArrayList<>();

    public Aisle(int size) {
    }

    public Aisle() {
        // TODO Auto-generated constructor stub
    }

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
}
