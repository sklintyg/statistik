package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.AgeGroupsResponse;

public interface AgeGroups {

    AgeGroupsResponse getAgeGroups(LocalDate from, LocalDate to);

}
