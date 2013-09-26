package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.sjukfall.SjukfallInfo;
import se.inera.statistics.service.sjukfall.SjukfallKey;
import se.inera.statistics.service.sjukfall.SjukfallService;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static se.inera.statistics.service.helper.DocumentHelper.*;

public class Processor {

    @Autowired
    private ProcessorListener listener;

    @Autowired
    private SjukfallService sjukfallService;

    public void accept(JsonNode utlatande, JsonNode hsa) {
        SjukfallKey sjukfallKey = extractSjukfallKey(utlatande);

        SjukfallInfo sjukfallInfo = sjukfallService.register(sjukfallKey);

        int alder = extractAlder(sjukfallKey.getPersonId(), sjukfallKey.getStart());
        String kon = extractKon(sjukfallKey.getPersonId());

        ObjectNode anonymous = utlatande.deepCopy();
        ObjectNode patientNode = (ObjectNode)anonymous.path("patient");
        patientNode.remove("id");
        patientNode.remove("fornamn");
        patientNode.remove("efternamn");
        patientNode.put("alder", alder);
        patientNode.put("kon", kon);

        listener.accept(sjukfallInfo, anonymous, hsa);
    }

    protected String extractKon(String personId) {
        return personId.charAt(11) % 2 == 0 ? "kvinna" : "man";
    }

    protected int extractAlder(String personId, Date start) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendYear(4,4).appendMonthOfYear(2).appendDayOfMonth(2).toFormatter();
        LocalDate birthDate = org.joda.time.LocalDate.parse(personId.substring(0,8), formatter);
        LocalDate referenceDate = new LocalDate(start);
        Period period = new Period(birthDate, referenceDate);
        return period.getYears();
    }


    protected SjukfallKey extractSjukfallKey(JsonNode utlatande) {
        try {
            String personId = getPersonId(utlatande);
            String vardgivareId = getVardgivareId(utlatande);
            String startString = getForstaNedsattningsdag(utlatande);
            String endString = getSistaNedsattningsdag(utlatande);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date start = dateFormat.parse(startString);
            Date end = dateFormat.parse(endString);

            return new SjukfallKey(personId, vardgivareId, start, end);
        } catch (ParseException | NullPointerException e) {
            throw new StatisticsMalformedDocument("Could not parse dates from intyg.", e);
        }
    }

}
