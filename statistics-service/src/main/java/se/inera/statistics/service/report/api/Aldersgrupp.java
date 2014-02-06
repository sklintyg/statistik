package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;

public interface Aldersgrupp {

    void count(String period, String hsaId, String group, RollingLength rollingLength, Verksamhet typ, Sex sex);

    AgeGroupsResponse getCurrentAgeGroups(String hsaId);

    AgeGroupsResponse getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rollingLength);
}
