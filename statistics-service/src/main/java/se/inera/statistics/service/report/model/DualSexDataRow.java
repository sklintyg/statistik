package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DualSexDataRow {

    private final String name;
    private final List<DualSexField> data;

    public DualSexDataRow(String name, List<DualSexField> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<DualSexField> getData() {
        return data;
    }

    public List<Integer> getDataForSex(Sex sex) {
        ArrayList<Integer> dataForSex = new ArrayList<>();
        for (DualSexField field : data) {
            dataForSex.add(field.getValue(sex));
        }
        return dataForSex;
    }

    @Override
    public String toString() {
        return "DualSexDataRow{" + "name='" + name + '\'' + ", data=" + data + '}';
    }
}
