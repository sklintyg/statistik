package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Sex;

public interface DiagnosisSubGroups {

    DiagnosisGroupResponse getDiagnosisGroups(LocalDate from, LocalDate to, String diagnosisGroupId);
    void count(String period, String diagnosgrupp, String undergrupp, Sex sex);

}
