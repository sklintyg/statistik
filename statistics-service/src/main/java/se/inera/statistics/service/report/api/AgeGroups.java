package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Range;

public interface AgeGroups {

    AgeGroupsResponse getAgeGroups(Range range);

}
