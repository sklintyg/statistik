package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DualSexDataRow {

    private final String period;
    private final List<DualSexField> data;

    public DualSexDataRow(String period, List<DualSexField> data) {
        this.period = period;
        this.data = data;
    }

    public String getPeriod() {
        return period;
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

}
