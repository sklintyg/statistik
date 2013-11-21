package se.inera.statistics.service.report.model;

import se.inera.statistics.service.report.model.db.AgeGroupsRow;

import java.util.ArrayList;
import java.util.List;

public class AgeGroupsResponse {

    private final List<AgeGroupsRow> ageGroupsRows;
    private final int months;

    public AgeGroupsResponse(List<AgeGroupsRow> rows, int numberOfMonthsCalculated) {
        this.ageGroupsRows = rows;
        this.months = numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (AgeGroupsRow row : ageGroupsRows) {
            groups.add(row.getGroup());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Sex sex) {
        List<Integer> data = new ArrayList<>();
        for (AgeGroupsRow row : ageGroupsRows) {
            if (sex == Sex.Female) {
                data.add(row.getFemale());
            } else {
                data.add(row.getMale());
            }
        }
        return data;
    }

    public List<AgeGroupsRow> getAgeGroupsRows() {
        return ageGroupsRows;
    }

    public int getMonths() {
        return months;
    }

    @Override
    public String toString() {
        return "{\"AgeGroupsResponse\":{" + "\"ageGroupsRows\":" + ageGroupsRows + ", \"months\":" + months + "}}";
    }
}
