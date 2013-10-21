package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.util.Verksamhet;

public interface AgeGroups {

    void count(String period, String hsaId, String group, int periods, Verksamhet typ, Sex sex);

    AgeGroupsResponse getCurrentAgeGroups(String hsaId);

    AgeGroupsResponse getHistoricalAgeGroups(String hsaId, LocalDate when, int periods);
    
}
