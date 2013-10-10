package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;

public interface DiagnosisGroups {

    DiagnosisGroupResponse getDiagnosisGroups(Range range);

    void count(String period, String diagnosgrupp, Sex sex);

}
