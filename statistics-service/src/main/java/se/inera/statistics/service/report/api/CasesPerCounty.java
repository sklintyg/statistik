package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;

public interface CasesPerCounty {

    SimpleDualSexResponse<SimpleDualSexDataRow> getStatistics(Range range);

    void count(String period, String enhetId, String lanId, RollingLength length, Verksamhet enhet, Sex kon);
}
