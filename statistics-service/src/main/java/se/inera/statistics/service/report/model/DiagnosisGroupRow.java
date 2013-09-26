package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisGroupRow {

    private final String period;
    private final List<DualSexField> diagnosisGroupData;

    public DiagnosisGroupRow(String period, List<DualSexField> diagnosisGroupData) {
        this.period = period;
        this.diagnosisGroupData = diagnosisGroupData;
    }

    public String getPeriod() {
        return period;
    }

    public List<DualSexField> getDiagnosisGroupData() {
        return diagnosisGroupData;
    }

    public List<Integer> getDiagnosisGroupData(Sex sex) {
        ArrayList<Integer> data = new ArrayList<>();
        for (DualSexField field : diagnosisGroupData) {
            data.add(field.getValue(sex));
        }
        return data;
    }

}
