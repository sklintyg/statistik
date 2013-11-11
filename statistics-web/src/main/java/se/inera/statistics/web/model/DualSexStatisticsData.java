package se.inera.statistics.web.model;

public class DualSexStatisticsData {

    private final TableData tableData;
    private final ChartData maleChart;
    private final ChartData femaleChart;
    private final String period;

    public DualSexStatisticsData(TableData tableData, ChartData maleChart, ChartData femaleChart, String period) {
        this.tableData = tableData;
        this.maleChart = maleChart;
        this.femaleChart = femaleChart;
        this.period = period;
    }

    public TableData getTableData() {
        return tableData;
    }

    public ChartData getMaleChart() {
        return maleChart;
    }

    public ChartData getFemaleChart() {
        return femaleChart;
    }

    public String getPeriod() {
        return period;
    }

}
