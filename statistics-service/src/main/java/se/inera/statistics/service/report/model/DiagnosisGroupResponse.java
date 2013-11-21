package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisGroupResponse {

    private final List<DiagnosisGroup> diagnosisGroups;
    private final List<DualSexDataRow> rows;

    public DiagnosisGroupResponse(List<DiagnosisGroup> diagnosisGroups, List<DualSexDataRow> rows) {
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
            subGroupStrings.add(diagnosisGroup.asString());
        }
        return subGroupStrings;
    }

    public List<DualSexDataRow> getRows() {
        return rows;
    }

    public List<String> getPeriods() {
        List<String> periods = new ArrayList<>();
        for (DualSexDataRow row : rows) {
            periods.add(row.getName());
        }
        return periods;
    }

    public List<Integer> getDataFromIndex(int index, Sex sex) {
        List<Integer> indexData = new ArrayList<>();
        for (DualSexDataRow row : rows) {
            List<DualSexField> data = row.getData();
            indexData.add(data.get(index).getValue(sex));
        }
        return indexData;
    }

    @Override
    public String toString() {
        return "{\"DiagnosisGroupResponse\":{" + "\"diagnosisGroups\":" + diagnosisGroups + ", \"rows\":" + rows + "}}";
    }
}
