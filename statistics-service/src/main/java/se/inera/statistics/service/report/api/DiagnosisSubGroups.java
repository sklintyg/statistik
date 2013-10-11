package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;

public interface DiagnosisSubGroups {

    DiagnosisGroupResponse getDiagnosisGroups(Range range, String diagnosisGroupId);
    void count(String period, String diagnosgrupp, String undergrupp, Sex sex);

}
