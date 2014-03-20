package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class DocumentHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentHelper.class);

    public static final int SEX_DIGIT = 11;
    public static final int DATE_PART_OF_PERSON_ID = 8;
    public static final int DAY_PART_OF_DATE_PART = 6;
    public static final int MONTH_PART_OF_DATE_PART = 4;
    public static final int SAMORDNINGSNUMMER_DAY_CONSTANT = 60;
    private static final DateTimeFormatter MONTHDAY_FORMATTER = DateTimeFormat.forPattern("MMdd");
    public static final int NO_AGE = -1;


    public static final Matcher DIAGNOS_MATCHER = Matcher.Builder.matcher("observationskategori").add(Matcher.Builder.matcher("code", "439401001")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));
    public static final Matcher ARBETSFORMAGA_MATCHER = Matcher.Builder.matcher("observationskod").add(Matcher.Builder.matcher("code", "302119000")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));

    private DocumentHelper() {
    }

    public static ObjectNode anonymize(JsonNode utlatande) {
        String personId = utlatande.path("patient").path("id").path("extension").textValue();
        int alder;
        try {
            alder = extractAlder(personId, ISODateTimeFormat.dateTimeParser().parseLocalDate(utlatande.path("signeringsdatum").textValue()));
        } catch (IllegalArgumentException e) {
            LOG.error("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: {}" , personId);
            alder = NO_AGE;
        }
        String kon = extractKon(personId);

        ObjectNode anonymous = utlatande.deepCopy();
        ObjectNode patientNode = (ObjectNode) anonymous.path("patient");
        patientNode.remove("id");
        patientNode.remove("fornamn");
        patientNode.remove("efternamn");
        patientNode.put("alder", alder);
        patientNode.put("kon", kon);
        return anonymous;
    }

    protected static String extractKon(String personId) {
        return personId.charAt(SEX_DIGIT) % 2 == 0 ? "kvinna" : "man";
    }

    protected static int extractAlder(String personId, LocalDate start) {
        LocalDate birthDate = null;
        int age;
        if (personId == null || personId.length() < DATE_PART_OF_PERSON_ID) {
            throw new IllegalArgumentException("Personnummer cannot be parsed as a date: " + personId);
        }
        try {
            String dateString = personId.substring(0, DATE_PART_OF_PERSON_ID);
            int day = Integer.parseInt(dateString.substring(DAY_PART_OF_DATE_PART));
            int month = Integer.parseInt(dateString.substring(MONTH_PART_OF_DATE_PART, DAY_PART_OF_DATE_PART));

            if (day > SAMORDNINGSNUMMER_DAY_CONSTANT) {
                dateString = dateString.substring(0, MONTH_PART_OF_DATE_PART) + (MONTHDAY_FORMATTER.print(new MonthDay(month, day - SAMORDNINGSNUMMER_DAY_CONSTANT)));
            }
            birthDate = ISODateTimeFormat.basicDate().parseLocalDate(dateString);
            LocalDate referenceDate = new LocalDate(start);
            Period period = new Period(birthDate, referenceDate);
            age = period.getYears();
        } catch (NumberFormatException | IllegalFieldValueException e) {
            throw new IllegalArgumentException("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: " + personId);
        }
        return age;
    }

    public static String getPersonId(JsonNode document) {
        return document.path("patient").path("id").path("extension").textValue();
    }

    public static String getVardgivareId(JsonNode document) {
        return document.path("skapadAv").path("vardenhet").path("vardgivare").path("id").path("extension").textValue();
    }

    public static String getEnhetId(JsonNode document) {
        return document.path("skapadAv").path("vardenhet").path("id").path("extension").textValue();
    }

    public static String getLakarId(JsonNode document) {
        return document.path("skapadAv").path("id").path("extension").textValue();
    }

    public static String getForstaNedsattningsdag(JsonNode document) {
        String from = null;
        for (JsonNode node: document.path("observationer")) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                JsonNode varde = node.path("observationsperiod");
                String candidate = varde.path("from").asText();
                if (from == null || from.compareTo(candidate) > 0) {
                    from = candidate;
                }
            }
        }
        return from;
    }

    public static String getSistaNedsattningsdag(JsonNode document) {
        String to = null;
        for (JsonNode node: document.path("observationer")) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                JsonNode varde = node.path("observationsperiod");
                String candidate = varde.path("tom").asText();
                if (to == null || to.compareTo(candidate) < 0) {
                    to = candidate;
                }
            }
        }
        return to;
    }

    public static String getKon(JsonNode document) {
        return document.path("patient").path("kon").textValue();
    }

    public static String getDiagnos(JsonNode document) {
        for (JsonNode node: document.path("observationer")) {
            if (DIAGNOS_MATCHER.match(node)) {
                return node.path("observationskod").path("code").textValue();
            }
        }
        return null;
    }

    public static List<Integer> getArbetsformaga(JsonNode document) {
        List<Integer> result = new ArrayList<>();
        for (JsonNode node: document.path("observationer")) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                for (JsonNode varde: node.path("varde")) {
                    result.add(varde.path("quantity").asInt());
                }
            }
        }
        return result;
    }

    public static int getAge(JsonNode document) {
        return document.path("patient").path("alder").intValue();
    }
}
