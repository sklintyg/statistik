package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisGroupResponse {

    private final List<DiagnosisGroup> diagnosisGroups;
    private final List<DiagnosisGroupRow> rows;

    public DiagnosisGroupResponse(List<DiagnosisGroup> diagnosisGroups, List<DiagnosisGroupRow> rows) {
        this.diagnosisGroups = diagnosisGroups;
        this.rows = rows;
    }

    public List<DiagnosisGroup> getDiagnosisGroups() {
        return diagnosisGroups;
    }

    public List<String> getDiagnosisGroupsAsStrings() {
        if (diagnosisGroups == null) {
            return new ArrayList<>();
        }
        List<String> subGroupStrings = new ArrayList<>();
        for (DiagnosisGroup diagnosisGroup : diagnosisGroups) {
            subGroupStrings.add(diagnosisGroup.toString());
        }
        return subGroupStrings;
    }

    public List<DiagnosisGroupRow> getRows() {
        return rows;
    }

    public List<String> getPeriods() {
        List<String> periods = new ArrayList<>();
        for (DiagnosisGroupRow row : rows) {
            periods.add(row.getPeriod());
        }
        return periods;
    }

    public List<Integer> getDataFromIndex(int index, Sex sex) {
        List<Integer> indexData = new ArrayList<>();
        for (DiagnosisGroupRow row : rows) {
            List<DualSexField> data = row.getDiagnosisGroupData();
            indexData.add(data.get(index).getValue(sex));
        }
        return indexData;
    }

}
