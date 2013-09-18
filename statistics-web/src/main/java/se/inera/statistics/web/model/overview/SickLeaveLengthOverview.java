package se.inera.statistics.web.model.overview;

import java.util.List;

public class SickLeaveLengthOverview {

    final private List<BarChartData> chartData;
    final private int longSickLeavesTotal;
    final private int longSickLeavesAlternation;

    public SickLeaveLengthOverview(List<BarChartData> chartData, int longSickLeavesTotal, int longSickLeavesAlternation) {
        this.chartData = chartData;
        this.longSickLeavesTotal = longSickLeavesTotal;
        this.longSickLeavesAlternation = longSickLeavesAlternation;
    }

    public List<BarChartData> getChartData() {
        return chartData;
    }

    public int getLongSickLeavesTotal() {
        return longSickLeavesTotal;
    }

    public int getLongSickLeavesAlternation() {
        return longSickLeavesAlternation;
    }

}
