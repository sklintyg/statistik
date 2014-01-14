package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.listener.SjukfallPerDiagnosgruppListener;
import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.repository.DiagnosisGroupKey;
import se.inera.statistics.service.report.util.Verksamhet;

import java.util.Map;

public interface Diagnosgrupp {
    DiagnosisGroupResponse getDiagnosisGroups(String hsaId, Range range);

    void count(String hsaId, String period, String diagnosgrupp, Verksamhet typ, Sex sex);

    void countAll(Map<DiagnosisGroupKey, SjukfallPerDiagnosgruppListener.DiagnosgruppValue> cache);
}
