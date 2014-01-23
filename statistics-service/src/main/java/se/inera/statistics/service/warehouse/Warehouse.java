package se.inera.statistics.service.warehouse;

public class Warehouse {
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
}
