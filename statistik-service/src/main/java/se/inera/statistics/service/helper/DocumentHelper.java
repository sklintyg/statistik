/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class DocumentHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentHelper.class);

    private static final String OBSERVATIONER = "observationer";
    private static final String EXTENSION = "extension";
    private static final String PATIENT = "patient";
    public static final int SEX_DIGIT = 11;

    public static final Matcher DIAGNOS_MATCHER = Matcher.Builder.matcher("observationskategori").add(Matcher.Builder.matcher("code", "439401001")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));
    public static final Matcher ARBETSFORMAGA_MATCHER = Matcher.Builder.matcher("observationskod").add(Matcher.Builder.matcher("code", "302119000")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));
    public static final String DOCUMENT_ID = "1.2.752.129.2.1.2.1";

    private DocumentHelper() {
    }

    public static ObjectNode anonymize(JsonNode utlatande) {
        ObjectNode anonymous = prepare(utlatande).deepCopy();
        ObjectNode patientNode = (ObjectNode) anonymous.path(PATIENT);
        patientNode.remove("id");
        patientNode.remove("fornamn");
        patientNode.remove("efternamn");
        return anonymous;
    }

    public static ObjectNode prepare(JsonNode utlatande) {
        String personId = utlatande.path(PATIENT).path("id").path(EXTENSION).textValue();
        int alder;
        try {
            alder = ConversionHelper.extractAlder(personId, ISODateTimeFormat.dateTimeParser().parseLocalDate(utlatande.path("observationer").findValue("observationsperiod").path("tom").textValue()));
        } catch (IllegalArgumentException e) {
            LOG.error("Personnummer cannot be parsed as a date, adjusting for samordningsnummer did not help: {}" , personId);
            alder = ConversionHelper.NO_AGE;
        }
        String kon = ConversionHelper.extractKon(personId);

        ObjectNode preparedDoc = utlatande.deepCopy();
        ObjectNode patientNode = (ObjectNode) preparedDoc.path(PATIENT);
        patientNode.put("alder", alder);
        patientNode.put("kon", kon);
        return preparedDoc;
    }

    public static String getPersonId(JsonNode document) {
        return document.path(PATIENT).path("id").path(EXTENSION).textValue();
    }

    public static String getVardgivareId(JsonNode document) {
        return document.path("skapadAv").path("vardenhet").path("vardgivare").path("id").path(EXTENSION).textValue();
    }

    public static String getEnhetId(JsonNode document) {
        return document.path("skapadAv").path("vardenhet").path("id").path(EXTENSION).textValue();
    }

    public static String getLakarId(JsonNode document) {
        return document.path("skapadAv").path("id").path(EXTENSION).textValue();
    }

    public static String getForstaNedsattningsdag(JsonNode document) {
        String from = null;
        for (JsonNode node: document.path(OBSERVATIONER)) {
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
        for (JsonNode node: document.path(OBSERVATIONER)) {
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
        return document.path(PATIENT).path("kon").textValue();
    }

    public static String getDiagnos(JsonNode document) {
        for (JsonNode node: document.path(OBSERVATIONER)) {
            if (DIAGNOS_MATCHER.match(node)) {
                return node.path("observationskod").path("code").textValue();
            }
        }
        return null;
    }

    public static List<JsonNode> getArbetsformaga(JsonNode document) {
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode node: document.path(OBSERVATIONER)) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                result.add(node);
            }
        }
        return result;
    }

    public static int getAge(JsonNode document) {
        return document.path(PATIENT).path("alder").intValue();
    }

    public static String getIntygId(JsonNode document) {
        String id = document.path("id").path("root").textValue();
        if (DOCUMENT_ID.equals(id)) {
            id = document.path("id").path("extension").textValue();
        }
        return id;
    }

    public static JsonNode prepare(JsonNode utlatande, JsonNode hsaInfo) {
        String personId = utlatande.path("patient").path("id").path("extension").textValue();
        int alder = ConversionHelper.extractAlder(personId, ISODateTimeFormat.dateTimeParser().parseLocalDate(utlatande.path("signeringsdatum").textValue()));
        String kon = ConversionHelper.extractKon(personId);

        ObjectNode prepared = utlatande.deepCopy();
        ObjectNode patientNode = (ObjectNode) prepared.path("patient");
        patientNode.put("alder", alder);
        patientNode.put("kon", kon);
        prepared.put("hsa", hsaInfo);
        return prepared;
    }
}
