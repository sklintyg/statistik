package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;

public interface DiagnosisGroups {
    public static final String HSA_NATIONELL = "nationell";

    DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range);

    void count(String hsaId, String period, String diagnosgrupp, Sex sex);

}
