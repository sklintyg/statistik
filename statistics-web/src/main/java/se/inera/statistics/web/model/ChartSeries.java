package se.inera.statistics.web.model;

import java.util.List;

import se.inera.statistics.service.report.model.Sex;

public class ChartSeries {

    private static int stackCounter = 0;
    private static final String STACKED = "stacked";

    private final String name;
    private final List<Integer> data;
    private final String stack;
    private final Sex sex;

    public ChartSeries(String name, List<Integer> data, String stack, Sex sex) {
        this.name = name;
        this.data = data;
        this.stack = stack;
        this.sex = sex;
    }

    public ChartSeries(String name, List<Integer> data, boolean stacked) {
        this(name, data, getStackValue(stacked), null);
    }

    public ChartSeries(String name, List<Integer> data, String stack) {
        this(name, data, stack, null);
    }

    public ChartSeries(String name, List<Integer> data, boolean stacked, Sex sex) {
        this(name, data, getStackValue(stacked), sex);
    }

    private static String getStackValue(boolean stacked) {
        return stacked ? STACKED : String.valueOf(stackCounter++);
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

    public Sex getSex() {
        return sex;
    }

    @Override
    public String toString() {
        return name + ": " + data.toString();
    }

}
