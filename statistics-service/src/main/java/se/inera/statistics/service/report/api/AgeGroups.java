package se.inera.statistics.service.report.api;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

public interface AgeGroups {
    String HSA_NATIONELL = "nationell";

    AgeGroupsResponse getAgeGroups(String hsaId, Range range);

    void count(String period, String hsaId, String group, Verksamhet typ, Sex sex);
}
