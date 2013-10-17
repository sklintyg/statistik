package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;

public interface VerksamhetOverview {

    VerksamhetOverviewResponse getOverview(String verksamhetId, Range range);
}
