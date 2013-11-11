package se.inera.statistics.web.model;

public class SimpleDetailsData {

    private final TableData tableData;
    private final ChartData chartData;
    private final int monthsIncluded;
    private final String period;

    public SimpleDetailsData(TableData tableData, ChartData chartData, int monthsIncluded, String period) {
        this.tableData = tableData;
        this.chartData = chartData;
        this.monthsIncluded = monthsIncluded;
        this.period = period;
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

    public String getPeriod() {
        return period;
    }

}
