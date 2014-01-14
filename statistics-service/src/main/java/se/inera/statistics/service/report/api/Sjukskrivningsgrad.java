package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.DegreeOfSickLeaveResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

public interface Sjukskrivningsgrad {
    DegreeOfSickLeaveResponse getStatistics(String hsaId, Range range);
    void count(String hsaId, String period, String grad, Verksamhet typ, Sex sex);
}
