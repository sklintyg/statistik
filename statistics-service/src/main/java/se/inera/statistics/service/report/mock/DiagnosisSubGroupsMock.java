package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DiagnosisGroupRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;

public class DiagnosisSubGroupsMock implements DiagnosisSubGroups {

    @Override
    public DiagnosisGroupResponse getDiagnosisSubGroups(String diagnosisGroupId) {
        List<DiagnosisGroup> headers = DiagnosisGroupsUtil.getSubGroups(diagnosisGroupId);
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

    private int g() {
        final int maxNumber = 100;
        return new Random().nextInt(maxNumber);
    }

}
