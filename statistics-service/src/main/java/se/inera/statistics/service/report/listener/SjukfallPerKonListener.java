package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.api.CasesPerMonth;
import se.inera.statistics.service.report.model.Sex;

@Component
public class SjukfallPerKonListener {
    public static final int YEAR_FIELD_LEN = 4;
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendYear(YEAR_FIELD_LEN, YEAR_FIELD_LEN).appendLiteral('-')
            .appendMonthOfYear(2).appendLiteral('-').appendDayOfMonth(2).toFormatter();

    private DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("yyyy-MM");

    @Autowired
    private CasesPerMonth casesPerMonthPersistenceHandler;

    public void acceptPeriod(LocalDate firstMonth, LocalDate endMonth, Sex sex) {
        for (LocalDate month = firstMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            accept(month, sex);
        }
    }

    protected void accept(LocalDate month, Sex sex) {
        String period = outputFormatter.print(month);
        casesPerMonthPersistenceHandler.count(period, sex);
    }

    protected static LocalDate getFirstDateMonth(LocalDate previousEnd, LocalDate start) {
        if (previousEnd == null) {
            return start.withDayOfMonth(1);
        } else {
            return previousEnd.withDayOfMonth(1).plusMonths(1);
        }
    }
}
