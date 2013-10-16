package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.CasesPerCountyResponse;
import se.inera.statistics.service.report.model.Range;

public interface CasesPerCounty {
    String HSA_NATIONELL = "nationell";

    CasesPerCountyResponse getStatistics(String hsaId, Range range);

}
