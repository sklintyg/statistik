package se.inera.statistics.service.report.model;

import java.util.List;

public class AgeGroupsResponse extends SimpleDualSexResponse<AgeGroupsRow> {

    public AgeGroupsResponse(List<AgeGroupsRow> rows, int numberOfMonthsCalculated) {
        super(rows, numberOfMonthsCalculated);
    }

}
