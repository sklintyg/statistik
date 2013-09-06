package se.inera.statistics.web.model;

import java.util.ArrayList;
import java.util.List;

public class DataSeries {

    private String name;
    private List<? extends Number> data;

    public DataSeries(String name, List<? extends Number> series) {
        super();
        this.name = name;
        this.data = new ArrayList<>(series);
    }

    public String getName() {
        return name;
    }

    public List<? extends Number> getData() {
        return data;
    }

}
