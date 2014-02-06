package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.Diagnosgrupp;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnosgruppMock implements Diagnosgrupp {

    private static final int MAX_VALUE = 100;

    @Override
    public DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range) {
        List<DiagnosisGroup> headers = DiagnosisGroupsUtil.getAllDiagnosisGroups();
        List<DualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new DualSexDataRow(periodName, randomData(headers.size())));
        }
        return new DiagnosisGroupResponse(headers, rows);
    }

    private List<DualSexField> randomData(int size) {
        DualSexField[] data = new DualSexField[size];
        for (int i = 0; i < size; i++) {
            data[i] = new DualSexField(randomValue(), randomValue());
        }
        return Arrays.asList(data);
    }

    private int randomValue() {
        return new Random().nextInt(MAX_VALUE);
    }

    @Override
    public void count(String hsaId, String period, String diagnosgrupp, Verksamhet typ, Sex sex) {
    }

}
