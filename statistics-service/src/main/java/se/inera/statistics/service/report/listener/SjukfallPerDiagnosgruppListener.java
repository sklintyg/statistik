package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.DiagnosisGroups;
import se.inera.statistics.service.report.model.Sex;

@Component
public class SjukfallPerDiagnosgruppListener {
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    @Autowired
    private DiagnosisGroups diagnosisgroupPersistenceHandler;

    public void acceptPeriod(LocalDate firstMonth, LocalDate endMonth, String group, Sex sex) {
        for (LocalDate month = firstMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            accept(month, group, sex);
        }
    }

    protected void accept(LocalDate month, String diagnosgrupp, Sex sex) {
        String period = PERIOD_FORMATTER.print(month);
        diagnosisgroupPersistenceHandler.count(period, diagnosgrupp, sex);
    }
}
