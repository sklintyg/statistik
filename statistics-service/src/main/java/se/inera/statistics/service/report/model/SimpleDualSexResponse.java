package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class SimpleDualSexResponse<T extends SimpleDualSexDataRow> {

    private final List<T> rows;
    private final int numberOfMonthsCalculated;

    public SimpleDualSexResponse(List<T> rows, int numberOfMonthsCalculated) {
        this.rows = rows;
        this.numberOfMonthsCalculated = numberOfMonthsCalculated;
    }

    public List<T> getRows() {
        return rows;
    }

    public int getNumberOfMonthsCalculated() {
        return numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (SimpleDualSexDataRow row : rows) {
            groups.add(row.getName());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Sex sex) {
        List<Integer> data = new ArrayList<>();
        for (SimpleDualSexDataRow row : rows) {
            data.add(row.getDataForSex(sex));
        }
        return data;
    }

    public List<Integer> getSummedData() {
        List<Integer> data = new ArrayList<>();
        for (SimpleDualSexDataRow row : rows) {
            data.add(row.getFemale() + row.getMale());
        }
        return data;
    }

    @Override
    public String toString() {
        return "SimpleDualSexResponse{" + "rows=" + rows + ", numberOfMonthsCalculated=" + getNumberOfMonthsCalculated() + '}';
    }
}
