package se.inera.statistics.service.processlog;

import static se.inera.statistics.service.helper.DocumentHelper.getForstaNedsattningsdag;
import static se.inera.statistics.service.helper.DocumentHelper.getPersonId;
import static se.inera.statistics.service.helper.DocumentHelper.getSistaNedsattningsdag;
import static se.inera.statistics.service.helper.DocumentHelper.getVardgivareId;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.sjukfall.SjukfallInfo;
import se.inera.statistics.service.sjukfall.SjukfallKey;
import se.inera.statistics.service.sjukfall.SjukfallService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Processor {
    public static final int YEAR_FIELD_LEN = 4;
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendYear(YEAR_FIELD_LEN, YEAR_FIELD_LEN).appendLiteral('-')
            .appendMonthOfYear(2).appendLiteral('-').appendDayOfMonth(2).toFormatter();
    public static final int SEX_DIGIT = 11;
    public static final int DATE_PART_OF_PERSON_ID = 8;

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
        ObjectNode patientNode = (ObjectNode) anonymous.path("patient");
        patientNode.remove("id");
        patientNode.remove("fornamn");
        patientNode.remove("efternamn");
        patientNode.put("alder", alder);
        patientNode.put("kon", kon);

        listener.accept(sjukfallInfo, anonymous, hsa);
    }

    protected String extractKon(String personId) {
        return personId.charAt(SEX_DIGIT) % 2 == 0 ? "kvinna" : "man";
    }

    protected int extractAlder(String personId, LocalDate start) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendYear(YEAR_FIELD_LEN, YEAR_FIELD_LEN).appendMonthOfYear(2).appendDayOfMonth(2).toFormatter();
        LocalDate birthDate = org.joda.time.LocalDate.parse(personId.substring(0, DATE_PART_OF_PERSON_ID), formatter);
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
            LocalDate start = formatter.parseLocalDate(startString);
            LocalDate end = formatter.parseLocalDate(endString);

            return new SjukfallKey(personId, vardgivareId, start, end);
        } catch (NullPointerException e) {
            throw new StatisticsMalformedDocument("Could not parse dates from intyg.", e);
        }
    }

}
