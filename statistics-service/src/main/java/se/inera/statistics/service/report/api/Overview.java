package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.OverviewResponse;
import se.inera.statistics.service.report.model.Range;

public interface Overview {

    OverviewResponse getOverview(Range range);

}
