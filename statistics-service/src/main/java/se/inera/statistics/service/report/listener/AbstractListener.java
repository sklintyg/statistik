package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public abstract class AbstractListener<T> {
    private static final DateTimeFormatter PERIOD_DATE_TIME_FORMATTERFORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormat.forPattern("yyyy-MM");

    abstract T setup(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, LocalDate start, LocalDate end);

    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa) {
        LocalDate start = PERIOD_DATE_TIME_FORMATTERFORMATTER.parseLocalDate(DocumentHelper.getForstaNedsattningsdag(utlatande));
        LocalDate endMonth = PERIOD_DATE_TIME_FORMATTERFORMATTER.parseLocalDate(DocumentHelper.getSistaNedsattningsdag(utlatande));

        LocalDate firstMonth = getFirstDateMonth(sjukfallInfo.getPrevEnd(), start);

        T token = setup(sjukfallInfo, utlatande, hsa, firstMonth, endMonth);

        accept(token, firstMonth, endMonth);
    }

    void accept(T token, LocalDate firstMonth, LocalDate endMonth) {
        for (LocalDate currentMonth = firstMonth; !currentMonth.isAfter(endMonth); currentMonth = currentMonth.plusMonths(1)) {
            accept(token, currentMonth);
        }
    }

    void accept(T token, LocalDate currentMonth) {
        String period = PERIOD_FORMATTER.print(currentMonth);
        accept(token, period);
    }

    abstract void accept(T token, String period);

    static LocalDate getFirstDateMonth(LocalDate previousEnd, LocalDate start) {
        if (previousEnd == null) {
            return start.withDayOfMonth(1);
        } else {
            return previousEnd.withDayOfMonth(1).plusMonths(1);
        }
    }
}
