package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

public interface AgeGroups {
    String HSA_NATIONELL = "nationell";

    AgeGroupsResponse getAgeGroups(String hsaId, LocalDate when);

    void count(String period, String hsaId, String group, Verksamhet typ, Sex sex);
}
