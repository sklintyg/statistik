package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class SickLeaveLengthResponse {

    private final List<SickLeaveLengthRow> rows;
    private final int numberOfMonthsCalculated;

    public SickLeaveLengthResponse(List<SickLeaveLengthRow> rows, int numberOfMonthsCalculated) {
        this.rows = rows;
        this.numberOfMonthsCalculated = numberOfMonthsCalculated;
    }

    public List<SickLeaveLengthRow> getRows() {
        return rows;
    }

    public int getNumberOfMonthsCalculated() {
        return numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (SickLeaveLengthRow row : rows) {
            groups.add(row.getGroup());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Sex sex) {
        List<Integer> data = new ArrayList<>();
        for (SickLeaveLengthRow row : rows) {
            data.add(row.getValueForSex(sex));
        }
        return data;
    }

}
