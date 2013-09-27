package se.inera.statistics.web.model;

public class DiagnosisGroupsData {

    private final TableData maleTable;
    private final TableData femaleTable;
    private final TableData maleChart;
    private final TableData femaleChart;

    public DiagnosisGroupsData(TableData maleTable, TableData femaleTable, TableData maleChart, TableData femaleChart) {
        this.maleTable = maleTable;
        this.femaleTable = femaleTable;
        this.maleChart = maleChart;
        this.femaleChart = femaleChart;
    }

    public TableData getMaleTable() {
        return maleTable;
    }

    public TableData getFemaleTable() {
        return femaleTable;
    }

    public TableData getMaleChart() {
        return maleChart;
    }

    public TableData getFemaleChart() {
        return femaleChart;
    }

}
