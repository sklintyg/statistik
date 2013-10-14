package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;

public class DiagnosisGroupsMock implements DiagnosisGroups {

    @Override
    public DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range) {
        List<DiagnosisGroup> headers = DiagnosisGroupsUtil.getAllDiagnosisGroups();
        List<DiagnosisGroupRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new DiagnosisGroupRow(periodName, randomData(headers.size())));
        }
        return new DiagnosisGroupResponse(headers, rows);
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
    public void count(String hsaId, String period, String diagnosgrupp, Sex sex) {
    }
}
