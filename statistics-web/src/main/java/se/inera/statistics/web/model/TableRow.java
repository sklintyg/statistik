package se.inera.statistics.web.model;

import java.util.Collections;
import java.util.List;

public class TableRow {

    private final String name;
    private final List<Number> data;

    public TableRow(String name, List<Number> data) {
        this.name = name;
        this.data = Collections.unmodifiableList(data);
    }

    public String getName() {
        return name;
    }

    public List<Number> getData() {
        return data;
    }

}
