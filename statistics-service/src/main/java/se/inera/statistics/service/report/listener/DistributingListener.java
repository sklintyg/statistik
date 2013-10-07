package se.inera.statistics.service.report.listener;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.processlog.ProcessorListener;
import se.inera.statistics.service.report.model.Sex;
import se.inera.statistics.service.sjukfall.SjukfallInfo;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class DistributingListener implements ProcessorListener {
    public static final int YEAR_FIELD_LEN = 4;
    private static final DateTimeFormatter PERIOD_DATE_TIME_FORMATTERFORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    private SjukfallPerKonListener sjukfallPerKonListener;

    @Override
    public void accept(SjukfallInfo sjukfallInfo, JsonNode utlatande, JsonNode hsa) {
        LocalDate start = PERIOD_DATE_TIME_FORMATTERFORMATTER.parseLocalDate(DocumentHelper.getForstaNedsattningsdag(utlatande));
        LocalDate endMonth = PERIOD_DATE_TIME_FORMATTERFORMATTER.parseLocalDate(DocumentHelper.getSistaNedsattningsdag(utlatande));

        LocalDate firstMonth = getFirstDateMonth(sjukfallInfo.getPrevEnd(), start);
        String enhet = DocumentHelper.getEnhetId(utlatande);
        Sex sex = DocumentHelper.getKon(utlatande).equals("man") ? Sex.Male : Sex.Female;

        sjukfallPerKonListener.acceptPeriod(firstMonth, endMonth, sex);
    }

    protected static LocalDate getFirstDateMonth(LocalDate previousEnd, LocalDate start) {
        if (previousEnd == null) {
            return start.withDayOfMonth(1);
        } else {
            return previousEnd.withDayOfMonth(1).plusMonths(1);
        }
    }
}
