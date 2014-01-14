package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.Sjukskrivningsgrad;
import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class SjukskrivningsgradMock implements Sjukskrivningsgrad {

    @Override
    public DegreeOfSickLeaveResponse getStatistics(String hsaId, Range range) {
        List<String> headers = Arrays.asList("25", "50", "75", "100");
        List<DualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new DualSexDataRow(periodName, randomData(headers.size())));
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

    @Override
    public void count(String hsaId, String period, String grad, Verksamhet typ, Sex sex) {
    }

}
