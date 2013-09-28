package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.report.repository.CasesPerMonthPersistenceHandler;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

public class SjukfallPerKonListener implements ProcessorListener {
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendYear(4,4).appendLiteral('-').appendMonthOfYear(2).appendLiteral('-').appendDayOfMonth(2).toFormatter();

    @Autowired
    private CasesPerMonthPersistenceHandler casesPerMonthPersistenceHandler;

    @Override
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa) {
        LocalDate start = formatter.parseLocalDate(DocumentHelper.getForstaNedsattningsdag(utlatande));
        LocalDate endMonth = formatter.parseLocalDate(DocumentHelper.getSistaNedsattningsdag(utlatande));
        
        LocalDate firstMonth = getFirstDateMonth(sjukfallInfo.getPrevEnd(), start);
        loopOverPeriod(sjukfallInfo, utlatande, hsa, firstMonth, endMonth);
    }

    protected void loopOverPeriod(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa, LocalDate firstMonth, LocalDate endMonth) {
        for (LocalDate month = firstMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            accept(month, sjukfallInfo, utlatande, hsa);
        }
    }

    protected void accept(LocalDate month, SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa) {
        casesPerMonthPersistenceHandler.count(null,null,null);
    }

    static protected LocalDate getFirstDateMonth(LocalDate previousEnd, LocalDate start) {
        if (previousEnd == null) {
            return start.withDayOfMonth(1);
        } else {
            return previousEnd.withDayOfMonth(1).plusMonths(1);
        }
    }
}
