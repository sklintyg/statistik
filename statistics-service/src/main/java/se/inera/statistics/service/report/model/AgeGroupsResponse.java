package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class AgeGroupsResponse {

    private final List<AgeGroupsRow> rows;
    private final int numberOfMonthsCalculated;

    public AgeGroupsResponse(List<AgeGroupsRow> rows, int numberOfMonthsCalculated) {
        this.rows = rows;
        this.numberOfMonthsCalculated = numberOfMonthsCalculated;
    }

    public List<AgeGroupsRow> getRows() {
        return rows;
    }

    public int getNumberOfMonthsCalculated() {
        return numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (AgeGroupsRow row : rows) {
            groups.add(row.getGroup());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Sex sex) {
        List<Integer> data = new ArrayList<>();
        for (AgeGroupsRow row : rows) {
            data.add(row.getValueForSex(sex));
        }
        return data;
    }

}
