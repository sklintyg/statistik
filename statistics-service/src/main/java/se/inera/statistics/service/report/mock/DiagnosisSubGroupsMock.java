package se.inera.statistics.service.report.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.report.api.DiagnosisSubGroups;
import se.inera.statistics.service.report.model.DiagnosisGroup;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.DualSexDataRow;
import se.inera.statistics.service.report.model.DualSexField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.DiagnosisGroupsUtil;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.Verksamhet;

public class DiagnosisSubGroupsMock implements DiagnosisSubGroups {

    @Autowired
    DiagnosisGroupsUtil diagnosisGroupsUtil;
    
    @Override
    public DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range, String diagnosisGroupId) {
        List<DiagnosisGroup> headers = diagnosisGroupsUtil.getSubGroups(diagnosisGroupId);
        List<DualSexDataRow> rows = new ArrayList<>();
        for (String periodName : ReportUtil.PERIODS) {
            rows.add(new DualSexDataRow(periodName, randomData(headers.size())));
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

    @Override
    public void count(String hsaId, String period, String diagnosgrupp, String undergrupp, Verksamhet typ, Sex sex) {
    }

}
