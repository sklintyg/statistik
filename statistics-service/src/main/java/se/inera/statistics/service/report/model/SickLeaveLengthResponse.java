package se.inera.statistics.service.report.model;

import java.util.List;

public class SickLeaveLengthResponse extends SimpleDualSexResponse<SickLeaveLengthRow> {

    public SickLeaveLengthResponse(List<SickLeaveLengthRow> rows, int numberOfMonthsCalculated) {
        super(rows, numberOfMonthsCalculated);
    }

}
