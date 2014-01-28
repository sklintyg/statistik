package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Aisle implements Iterable<WideLine> {
    private final List<WideLine> lines = new ArrayList<>();

    public Aisle(int size) {
    }

    public Aisle() {
        // TODO Auto-generated constructor stub
    }

    public void addLine(WideLine line) {
        lines.add(line);
    }

    @Override
    public Iterator<WideLine> iterator() {
        return lines.iterator();
    }

    public int getSize() {
        return lines.size();
    }
}
