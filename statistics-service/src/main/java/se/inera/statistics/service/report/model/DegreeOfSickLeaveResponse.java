package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DegreeOfSickLeaveResponse {

    private final List<String> degreesOfSickLeave;
    private final List<DualSexDataRow> rows;

    public DegreeOfSickLeaveResponse(List<String> degreesOfSickLeave, List<DualSexDataRow> rows) {
        this.degreesOfSickLeave = degreesOfSickLeave;
        this.rows = rows;
    }

    public List<String> getDegreesOfSickLeave() {
        return degreesOfSickLeave;
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
        return "{\"DegreeOfSickLeaveResponse\":{\"degreesOfSickLeave\":" + formattedDegreesOfSickLeave() + ", \"rows\":" + rows + "}}";
    }

    private String formattedDegreesOfSickLeave() {
        StringBuilder sb = new StringBuilder("[");
        for (String s : degreesOfSickLeave) {
            sb.append('"');
            sb.append(s);
            sb.append("\", ");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 2);
        }
        sb.append(']');
        return sb.toString();
    }
}
