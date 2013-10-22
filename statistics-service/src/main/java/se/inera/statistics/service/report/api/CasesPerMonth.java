package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.util.Verksamhet;

public interface CasesPerMonth {
    SimpleDualSexResponse<SimpleDualSexDataRow> getCasesPerMonth(String hsaId, Range range);

    void count(String hsaId, String period, Verksamhet typ, Sex sex);
}
