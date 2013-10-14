package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.DegreeOfSickLeave;
import se.inera.statistics.service.report.model.DegreeOFSickLeaveRow;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.util.ReportUtil;

public class DegreeOfSickLeaveMock implements DegreeOfSickLeave {

    @Override
    public DegreeOfSickLeaveResponse getStatistics(String hsaId) {
        List<String> headers = Arrays.asList("Antal sjukfall per 25%", "Antal sjukfall per 50%", "Antal sjukfall per 75%", "Antal sjukfall per 100%");
        List<DegreeOFSickLeaveRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new DegreeOFSickLeaveRow(periodName, randomData(headers.size())));
        }
        return new DegreeOfSickLeaveResponse(headers, rows);
    }

    private List<DualSexField> randomData(int size) {
        DualSexField[] data = new DualSexField[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DualSexField(g(), g());
        }
        return Arrays.asList(data);
    }

    // CHECKSTYLE:OFF MagicNumber
    private int g() {
        final int maxValue = 100;
        return new Random().nextInt(maxValue);
    }
    // CHECKSTYLE:ON MagicNumber

}
