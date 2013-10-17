package se.inera.statistics.service.report.model;

public class SimpleDualSexDataRow {

    private final String name;
    private final DualSexField data;

    public SimpleDualSexDataRow(String name, DualSexField data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public DualSexField getData() {
        return data;
    }

    public Integer getDataForSex(Sex sex) {
        return data.getValue(sex);
    }

    public Integer getFemale() {
        return data.getValue(Sex.Female);
    }

    public Integer getMale() {
        return data.getValue(Sex.Male);
    }

}
