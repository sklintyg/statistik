package se.inera.statistics.service.report.model;

public class SimpleDualSexDataRow {

    private final String name;
    private final DualSexField data;

    public SimpleDualSexDataRow(String name, DualSexField data) {
        this.name = name;
        this.data = data;
    }

    public SimpleDualSexDataRow(String name, int female, int male) {
        this(name, new DualSexField(female, male));
    }

    public SimpleDualSexDataRow(String name, long female, long male) {
        this(name, new DualSexField((int) female, (int) male));
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

    @Override
    public String toString() {
        return "SimpleDualSexDataRow{" + "name='" + name + '\'' + ", data=" + data + '}';
    }
}
