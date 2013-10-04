package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.AgeGroupsResponse;
import se.inera.statistics.service.report.model.AgeGroupsRow;
import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;

import java.util.List;

public interface AgeGroups {

    AgeGroupsResponse getAgeGroups(LocalDate from, LocalDate to);

}
