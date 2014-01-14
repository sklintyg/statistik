package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

public interface Diagnoskapitel {
    DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range, String diagnosisGroupId);
    void count(String hsaId, String period, String diagnosgrupp, String undergrupp, Verksamhet typ, Sex sex);

}
