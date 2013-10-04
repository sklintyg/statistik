package se.inera.statistics.web.model;

import java.util.Collections;
import java.util.List;

public class ChartData {

    private final List<ChartSeries> series;
    private final List<String> categories;

    public ChartData(List<ChartSeries> series, List<String> categories) {
        this.series = series;
        this.categories = categories;
    }

    public List<ChartSeries> getSeries() {
        return series;
    }

    public List<String> getCategories() {
        return categories;
    }

}
