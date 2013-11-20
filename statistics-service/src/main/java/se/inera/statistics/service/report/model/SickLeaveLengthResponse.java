package se.inera.statistics.service.report.model;

import se.inera.statistics.service.report.model.db.SickLeaveLengthRow;

import java.util.ArrayList;
import java.util.List;

public class SickLeaveLengthResponse {

    private final List<SickLeaveLengthRow> sickLeaveGroupsRows;
    private final int months;

    public SickLeaveLengthResponse(List<SickLeaveLengthRow> rows, int numberOfMonthsCalculated) {
        this.sickLeaveGroupsRows = rows;
        this.months = numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (SickLeaveLengthRow row : sickLeaveGroupsRows) {
            groups.add(row.getGroup());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Sex sex) {
        List<Integer> data = new ArrayList<>();
        for (SickLeaveLengthRow row : sickLeaveGroupsRows) {
            if (sex == Sex.Female) {
                data.add(row.getFemale());
            } else {
                data.add(row.getMale());
            }
        }
        return data;
    }

    public List<SickLeaveLengthRow> getRows() {
        return sickLeaveGroupsRows;
    }

    public int getMonths() {
        return months;
    }

    @Override
    public String toString() {
        return "{\"SickLeaveLengthResponse\":{\"sickLeaveGroupsRows\":" + sickLeaveGroupsRows + ", \"months\":" + months + "}}";
    }
}
