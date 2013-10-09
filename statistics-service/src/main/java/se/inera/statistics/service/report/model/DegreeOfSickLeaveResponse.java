package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DegreeOfSickLeaveResponse {

    private final List<String> degreesOfSickLeave;
    private final List<DegreeOFSickLeaveRow> rows;

    public DegreeOfSickLeaveResponse(List<String> degreesOfSickLeave, List<DegreeOFSickLeaveRow> rows) {
        this.degreesOfSickLeave = degreesOfSickLeave;
        this.rows = rows;
    }

    public List<String> getDegreesOfSickLeave() {
        return degreesOfSickLeave;
    }

    public List<DegreeOFSickLeaveRow> getRows() {
        return rows;
    }

    public List<String> getPeriods() {
        List<String> periods = new ArrayList<>();
        for (DualSexDataRow row : rows) {
            periods.add(row.getPeriod());
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

}
