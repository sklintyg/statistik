package se.inera.statistics.web.model;

public class DiagnosisGroupsData {

    private final TableData tableData;
    private final ChartData maleChart;
    private final ChartData femaleChart;

    public DiagnosisGroupsData(TableData tableData, ChartData maleChart, ChartData femaleChart) {
        this.tableData = tableData;
        this.maleChart = maleChart;
        this.femaleChart = femaleChart;
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

}
