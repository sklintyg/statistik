/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.statistics.service.processlog.Arbetsnedsattning;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.model.Kon;

public final class DocumentHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentHelper.class);

    private static final String OBSERVATIONER = "observationer";
    private static final String EXTENSION = "extension";
    private static final String PATIENT = "patient";
    static final int SEX_DIGIT = 11;
    private static final int MAX_SJUKSKRIVNING = 100;

    private static final Matcher DIAGNOS_MATCHER = Matcher.Builder.matcher("observationskategori").add(Matcher.Builder.matcher("code", "439401001")).add(Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1"));
    private static final Matcher ARBETSFORMAGA_MATCHER = Matcher.Builder.matcher("observationskod").add(Matcher.Builder.matcher("code", "302119000")).add(Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1"));
    private static final String DOCUMENT_ID = "1.2.752.129.2.1.2.1";
    public static final String UTANENHETSID = "UTANENHETSID";
    private static final int NEDSATT100 = 100;
    private static final int NEDSATT25 = 25;
    private static final int NEDSATT50 = 50;
    private static final int NEDSATT75 = 75;
    public static final String GRUND_DATA = "grundData";
    public static final String SKAPAD_AV = "skapadAv";
    public static final String VARDENHET = "vardenhet";
    public static final String OBSERVATIONSPERIOD = "observationsperiod";
    public static final String NEDSATT_MED_25 = "nedsattMed25";
    public static final String NEDSATT_MED_50 = "nedsattMed50";
    public static final String NEDSATT_MED_75 = "nedsattMed75";
    public static final String NEDSATT_MED_100 = "nedsattMed100";

    private DocumentHelper() {
    }

    public static String getIntygType(JsonNode intyg) {
        final IntygVersion version = getIntygVersion(intyg);
        if (IntygVersion.VERSION1 == version) {
            return intyg.path("typ").path("code").textValue();
        } else {
            return intyg.path("typ").textValue();
        }
    }

    public enum IntygVersion {
        VERSION1, VERSION2
    }

    public static Patientdata getPatientData(JsonNode intyg) {
        final IntygVersion version = getIntygVersion(intyg);
        String personId = getPersonId(intyg, version);
        int alder;
        try {
            final LocalDate intygDate = parseDate(getSistaNedsattningsdag(intyg, version));
            if (intygDate != null) {
                alder = ConversionHelper.extractAlder(personId, intygDate);
            } else {
                LOG.error("Date for 'sista nedsattningsdag' could not be parsed: {}", personId);
                alder = ConversionHelper.NO_AGE;
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: {}", personId);
            LOG.debug("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: {}", personId, e);
            alder = ConversionHelper.NO_AGE;
        }
        String kon = ConversionHelper.extractKon(personId);
        return new Patientdata(alder, Kon.parse(kon));
    }

    public static IntygVersion getIntygVersion(JsonNode document) {
        return document.has(GRUND_DATA) ? IntygVersion.VERSION2 : IntygVersion.VERSION1;
    }

    public static String getPersonId(JsonNode document, IntygVersion version) {
        String personIdRaw = getPersonIdFromIntyg(document, version);
        return getUnifiedPersonId(personIdRaw);
    }

    public static String getUnifiedPersonId(String personIdRaw) {
        if (personIdRaw.matches("[0-9]{8}-[0-9]{4}")) {
            return personIdRaw;
        } else if (personIdRaw.matches("[0-9]{12}")) {
            return personIdRaw.substring(0, ConversionHelper.DATE_PART_OF_PERSON_ID) + "-" + personIdRaw.substring(ConversionHelper.DATE_PART_OF_PERSON_ID);
        } else {
            throw new PersonIdParseException("Failed to parse person id");
        }
    }

    private static String getPersonIdFromIntyg(JsonNode document, IntygVersion version) {
        if (IntygVersion.VERSION1 == version) {
            return document.path(PATIENT).path("id").path(EXTENSION).textValue();
        } else {
            return document.path(GRUND_DATA).path("patient").path("personId").textValue();
        }
    }

    public static String getVardgivareId(JsonNode document, IntygVersion version) {
        if (IntygVersion.VERSION1 == version) {
            return document.path(SKAPAD_AV).path(VARDENHET).path("vardgivare").path("id").path(EXTENSION).textValue();
        } else {
            return document.path(GRUND_DATA).path(SKAPAD_AV).path(VARDENHET).path("vardgivare").path("vardgivarid").textValue();
        }
    }

    public static String getEnhetId(JsonNode document, IntygVersion version) {
        if (IntygVersion.VERSION1 == version) {
            final String result = document.path(SKAPAD_AV).path(VARDENHET).path("id").path(EXTENSION).textValue();
            return result != null ? result : UTANENHETSID;
        } else {
            final String result = document.path(GRUND_DATA).path(SKAPAD_AV).path(VARDENHET).path("enhetsid").textValue();
            return result != null ? result : UTANENHETSID;
        }
    }

    public static String getEnhetNamn(JsonNode document, IntygVersion version) {
        if (IntygVersion.VERSION1 == version) {
            return document.path(SKAPAD_AV).path(VARDENHET).path("namn").textValue();
        } else {
            return document.path(GRUND_DATA).path(SKAPAD_AV).path(VARDENHET).path("enhetsnamn").textValue();
        }
    }

    public static String getLakarId(JsonNode document, IntygVersion version) {
        if (IntygVersion.VERSION1 == version) {
            return document.path(SKAPAD_AV).path("id").path(EXTENSION).textValue();
        } else {
            return document.path(GRUND_DATA).path(SKAPAD_AV).path("personId").textValue();
        }
    }

    static String getForstaNedsattningsdag(JsonNode document, IntygVersion version) {
        if (version == IntygVersion.VERSION1) {
            return getForstaNedsattningsdagV1(document);
        } else {
            return document.path("giltighet").path("from").textValue();
        }
    }

    private static String getForstaNedsattningsdagV1(JsonNode document) {
        String from = null;
        for (JsonNode node : document.path(OBSERVATIONER)) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                JsonNode varde = node.path(OBSERVATIONSPERIOD);
                String candidate = varde.path("from").asText();
                if (from == null || from.compareTo(candidate) > 0) {
                    from = candidate;
                }
            }
        }
        return from;
    }

    static String getSistaNedsattningsdag(JsonNode document, IntygVersion version) {
        if (version == IntygVersion.VERSION1) {
            return getSistaNedsattningsdagV1(document);
        } else {
            return getSistaNedsattningsdagV2(document);
        }
    }

    private static String getSistaNedsattningsdagV2(JsonNode document) {
        final int startYear = 2000;
        LocalDate date = LocalDate.of(startYear, 1, 1);
        if (document.has(NEDSATT_MED_25)) {
            final LocalDate parsedDate = parseDate(document.path(NEDSATT_MED_25).path("tom").asText());
            if (parsedDate != null) {
                date = parsedDate;
            }
        }
        if (document.has(NEDSATT_MED_50)) {
            LocalDate tom = parseDate(document.path(NEDSATT_MED_50).path("tom").asText());
            if (tom != null) {
                date = tom.isAfter(date) ? tom : date;
            }
        }
        if (document.has(NEDSATT_MED_75)) {
            LocalDate tom = parseDate(document.path(NEDSATT_MED_75).path("tom").asText());
            if (tom != null) {
                date = tom.isAfter(date) ? tom : date;
            }
        }
        if (document.has(NEDSATT_MED_100)) {
            LocalDate tom = parseDate(document.path(NEDSATT_MED_100).path("tom").asText());
            if (tom != null) {
                date = tom.isAfter(date) ? tom : date;
            }
        }
        return date.toString();
    }

    private static String getSistaNedsattningsdagV1(JsonNode document) {
        String to = null;
        for (JsonNode node : document.path(OBSERVATIONER)) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                JsonNode varde = node.path(OBSERVATIONSPERIOD);
                String candidate = varde.path("tom").asText();
                if (to == null || to.compareTo(candidate) < 0) {
                    to = candidate;
                }
            }
        }
        return to;
    }

    public static String getDiagnos(JsonNode document, IntygVersion version) {
        if (version == IntygVersion.VERSION1) {
            for (JsonNode node : document.path(OBSERVATIONER)) {
                if (DIAGNOS_MATCHER.match(node)) {
                    return node.path("observationskod").path("code").textValue();
                }
            }
            return null;
        } else {
            return document.path("diagnosKod").textValue();
        }
    }

    public static List<Arbetsnedsattning> getArbetsnedsattning(JsonNode document, IntygVersion version) {
        List<Arbetsnedsattning> result = new ArrayList<>();
        if (version == IntygVersion.VERSION1) {
            for (JsonNode node : document.path(OBSERVATIONER)) {
                if (ARBETSFORMAGA_MATCHER.match(node)) {
                    int varde = MAX_SJUKSKRIVNING - node.path("varde").get(0).path("quantity").asInt();
                    LocalDate from = parseDate(node.path(OBSERVATIONSPERIOD).path("from").asText());
                    LocalDate tom = parseDate(node.path(OBSERVATIONSPERIOD).path("tom").asText());
                    if (from != null && tom != null) {
                        result.add(new Arbetsnedsattning(varde, from, tom));
                    }
                }
            }
            return result;
        } else {
            if (document.has(NEDSATT_MED_25)) {
                LocalDate from = parseDate(document.path(NEDSATT_MED_25).path("from").asText());
                LocalDate tom = parseDate(document.path(NEDSATT_MED_25).path("tom").asText());
                if (from != null && tom != null) {
                    result.add(new Arbetsnedsattning(NEDSATT25, from, tom));
                }
            }
            if (document.has(NEDSATT_MED_50)) {
                LocalDate from = parseDate(document.path(NEDSATT_MED_50).path("from").asText());
                LocalDate tom = parseDate(document.path(NEDSATT_MED_50).path("tom").asText());
                if (from != null && tom != null) {
                    result.add(new Arbetsnedsattning(NEDSATT50, from, tom));
                }
            }
            if (document.has(NEDSATT_MED_75)) {
                LocalDate from = parseDate(document.path(NEDSATT_MED_75).path("from").asText());
                LocalDate tom = parseDate(document.path(NEDSATT_MED_75).path("tom").asText());
                if (from != null && tom != null) {
                    result.add(new Arbetsnedsattning(NEDSATT75, from, tom));
                }
            }
            if (document.has(NEDSATT_MED_100)) {
                LocalDate from = parseDate(document.path(NEDSATT_MED_100).path("from").asText());
                LocalDate tom = parseDate(document.path(NEDSATT_MED_100).path("tom").asText());
                if (from != null && tom != null) {
                    result.add(new Arbetsnedsattning(NEDSATT100, from, tom));
                }
            }
            return result;
        }
    }

    private static LocalDate parseDate(String date) {
        try {
            return LocalDate.from(DateTimeFormatter.ISO_DATE.parse(date));
        } catch (NumberFormatException | DateTimeParseException e) {
            final String msg = "Failed to parse date: " + date;
            LOG.info(msg);
            LOG.debug(msg, e);
            return null;
        }
    }

    public static String getIntygId(JsonNode document, IntygVersion version) {
        if (version == IntygVersion.VERSION1) {
            String id = document.path("id").path("root").textValue();
            if (DOCUMENT_ID.equals(id)) {
                id = document.path("id").path("extension").textValue();
            }
            return id;
        } else {
            return document.path("id").textValue();
        }
    }

    public static boolean isEnkeltIntyg(JsonNode intyg, IntygVersion version) {
        if (version == IntygVersion.VERSION1) {
            LOG.warn("'Enkelt intyg' parsing has not been implemented for old intyg format. Defaulting to false.");
            return false;
        }
        final String funktionsnedsattning = intyg.path("funktionsnedsattning").toString();
        final String aktivitetsbegransning = intyg.path("aktivitetsbegransning").toString();
        return isAnyFieldIndicatingEnkeltIntyg(funktionsnedsattning, aktivitetsbegransning);
    }

    static boolean isAnyFieldIndicatingEnkeltIntyg(String... fields) {
        for (String field : fields) {
            if (isFieldIndicatingEnkeltIntyg(field)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Denna kod implementeras också i AnonymizeJson.groovy och bör hållas i synk med denna
     */
    private static boolean isFieldIndicatingEnkeltIntyg(String field) {
        if (field == null) {
            return false;
        }
        final String cleanedField = field.replaceAll("[^A-Za-zåäöÅÄÖ]", "");
        return "E".equalsIgnoreCase(cleanedField) || "Enkel".equalsIgnoreCase(cleanedField) || "Enkelt".equalsIgnoreCase(cleanedField);
    }

}
