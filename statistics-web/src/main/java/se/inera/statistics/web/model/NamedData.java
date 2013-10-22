package se.inera.statistics.web.model;

import java.util.Collections;
import java.util.List;

public class NamedData {

    private final String name;
    private final List<Object> data;

    public NamedData(String name, List<? extends Object> data) {
        this.name = name;
        this.data = Collections.unmodifiableList(data);
    }

    public String getName() {
        return name;
    }

    public List<Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return name + ": " + data.toString();
    }
}
