package se.inera.statistics.service.report.listener;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import java.util.Date;

public class SjukfallPerKonListener implements ProcessorListener {
    @Override
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa) {
        String start = DocumentHelper.getForstaNedsattningsdag(utlatande);
        String end = DocumentHelper.getSistaNedsattningsdag(utlatande);

    }

    protected int getMonthsDiff(String start, String end) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendYear(4,4).appendLiteral('-').appendMonthOfYear(2).appendLiteral('-').appendDayOfMonth(2).toFormatter();
        LocalDate startDate = org.joda.time.LocalDate.parse(start, formatter);
        LocalDate endDate = org.joda.time.LocalDate.parse(end, formatter);
        Period period = new Period(startDate, endDate);
        return period.getMonths();
    }

}
