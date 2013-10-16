package se.inera.statistics.service.report.model;

import java.util.List;

public class CasesPerCountyResponse extends SimpleDualSexResponse<CasesPerCountyRow> {

    public CasesPerCountyResponse(List<CasesPerCountyRow> rows, int numberOfMonthsCalculated) {
        super(rows, numberOfMonthsCalculated);
    }

}
