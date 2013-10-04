package se.inera.statistics.web.model;

import java.util.List;

public class ChartSeries {

    private final String name;
    private final List<Integer> data;

    public ChartSeries(String name, List<Integer> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getData() {
        return data;
    }

}
