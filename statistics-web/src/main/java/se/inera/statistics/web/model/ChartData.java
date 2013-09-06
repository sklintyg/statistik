package se.inera.statistics.web.model;

import java.util.ArrayList;
import java.util.List;

public class ChartData {

    private String title;
    private List<DataSeries> dataSeries;
    private List<String> categories;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataSeries> getDataSeries() {
        return dataSeries;
    }

    public void setAllDataSeries(List<DataSeries> series) {
        this.dataSeries = series;
    }

    public void addDataSeries(String name, List<? extends Number> series) {
        if (this.dataSeries == null) {
            this.dataSeries = new ArrayList<>();
        }
        this.dataSeries.add(new DataSeries(name, series));
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
