package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Range;

public interface AgeGroups {
    public static final String HSA_NATIONELL = "nationell";

    AgeGroupsResponse getAgeGroups(String hsaId, Range range);

}
