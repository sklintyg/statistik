package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.DiagnosisGroupResponse;
import se.inera.statistics.service.report.model.Sex;

public interface DiagnosisGroups {

    DiagnosisGroupResponse getDiagnosisGroups(LocalDate from, LocalDate to);

    void count(String period, String diagnosgrupp, Sex sex);

}
