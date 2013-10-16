package se.inera.statistics.web.model;

import java.util.List;

public class ChartSeries {

    private final String name;
    private final List<Integer> data;
    private final String stack;

    public ChartSeries(String name, List<Integer> data) {
        this.name = name;
        this.data = data;
        this.stack = null;
    }

    public ChartSeries(String name, List<Integer> data, String stack) {
        this.name = name;
        this.data = data;
        this.stack = stack;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getData() {
        return data;
    }

    public String getStack() {
        return stack;
    }

    @Override
    public String toString() {
        return name + ": " + data.toString();
    }

}
