package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;

public interface CasesPerCounty {
    String HSA_NATIONELL = "nationell";

    SimpleDualSexResponse<SimpleDualSexDataRow> getStatistics(String hsaId, Range range);

}
