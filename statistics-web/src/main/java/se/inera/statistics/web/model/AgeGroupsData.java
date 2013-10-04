package se.inera.statistics.web.model;

public class AgeGroupsData {

    private final TableData tableData;
    private final ChartData chartData;
    private final int monthsIncluded;

    public AgeGroupsData(TableData tableData, ChartData chartData, int monthsIncluded) {
        this.tableData = tableData;
        this.chartData = chartData;
        this.monthsIncluded = monthsIncluded;
    }

    public TableData getTableData() {
        return tableData;
    }

    public ChartData getChartData() {
        return chartData;
    }

    public int getMonthsIncluded() {
        return monthsIncluded;
    }
}
