/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class DocumentHelper {

    private static final String OBSERVATIONER = "observationer";
    private static final String EXTENSION = "extension";
    private static final String PATIENT = "patient";
    public static final int SEX_DIGIT = 11;
    public static final int DATE_PART_OF_PERSON_ID = 8;

    public static final Matcher DIAGNOS_MATCHER = Matcher.Builder.matcher("observationskategori").add(Matcher.Builder.matcher("code", "439401001")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));
    public static final Matcher ARBETSFORMAGA_MATCHER = Matcher.Builder.matcher("observationskod").add(Matcher.Builder.matcher("code", "302119000")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));

    private DocumentHelper() {
    }

    public static ObjectNode anonymize(JsonNode utlatande) {
        String personId = utlatande.path(PATIENT).path("id").path(EXTENSION).textValue();
        int alder = extractAlder(personId, ISODateTimeFormat.dateTimeParser().parseLocalDate(utlatande.path("signeringsdatum").textValue()));
        String kon = extractKon(personId);

        ObjectNode anonymous = utlatande.deepCopy();
        ObjectNode patientNode = (ObjectNode) anonymous.path(PATIENT);
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
        LocalDate birthDate = ISODateTimeFormat.basicDate().parseLocalDate(personId.substring(0, DATE_PART_OF_PERSON_ID));
        LocalDate referenceDate = new LocalDate(start);
        Period period = new Period(birthDate, referenceDate);
        return period.getYears();
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

    public static List<String> getArbetsformaga(JsonNode document) {
        List<String> result = new ArrayList<>();
        for (JsonNode node: document.path(OBSERVATIONER)) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                for (JsonNode varde: node.path("varde")) {
                    result.add(varde.path("quantity").asText());
                }
            }
        }
        return result;
    }

    public static int getAge(JsonNode document) {
        return document.path(PATIENT).path("alder").intValue();
    }

    private static IdMap<String> enhetsMap = new IdMap<>();
    
    public static int getEnhetAndRemember(JsonNode document) {
        return enhetsMap.getId(getEnhetId(document));
    }

    public static int getLakarIntyg(JsonNode document) {
        return 0;
    }

    public static int getPatient(JsonNode document) {
        String id = getPersonId(document);
        return Integer.parseInt(id.substring(2, 8)) * 1000 + Integer.parseInt(id.substring(9, 12)) + (1_000_000_000 * (Integer.parseInt(id.substring(0,2)) - 19));
    }
    
    private static class IdMap<T> {
        private final Map<T, Integer> map = new HashMap<T,Integer>();
        
        public synchronized Integer getId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                id = map.size() + 1;
                map.put(key, id);
            }
            return id;
        }
    }
}
