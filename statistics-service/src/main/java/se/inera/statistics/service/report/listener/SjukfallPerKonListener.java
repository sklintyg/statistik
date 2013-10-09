package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.Sex;

@Component
public class SjukfallPerKonListener {
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    @Autowired
    private CasesPerMonth casesPerMonthPersistenceHandler;

    public void acceptPeriod(LocalDate firstMonth, LocalDate endMonth, Sex sex) {
        for (LocalDate month = firstMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            accept(month, sex);
        }
    }

    protected void accept(LocalDate month, Sex sex) {
        String period = PERIOD_FORMATTER.print(month);
        casesPerMonthPersistenceHandler.count(period, sex);
    }
}
