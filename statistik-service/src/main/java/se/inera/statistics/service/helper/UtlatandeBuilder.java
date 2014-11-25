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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UtlatandeBuilder {
    private static final LocalTime SIGN_TIME_OF_DAY = new LocalTime(7, 7);
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ss.SSS");

    private final JsonNode template;

    public UtlatandeBuilder() {
        this("/json/fk7263_M_template.json");
    }

    public UtlatandeBuilder(String templateFile) {
        template = JSONParser.parse(readTemplate(templateFile));
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String vardenhet, String diagnos, int arbetsformaga) {
        return build(patientId, start, end, vardenhet, "vardgivarId", diagnos, arbetsformaga);
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String vardenhet, String vardgivare, String diagnos, int arbetsformaga) {
        return build(patientId, start, end, "Personal HSA-ID", vardenhet, vardgivare, diagnos, Arrays.asList(String.valueOf(arbetsformaga)));
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String vardenhet, String vardgivare, String diagnos, List <String> arbetsformaga) {
        return build(patientId, start, end, "Personal HSA-ID", vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    //CHECKSTYLE:OFF ParameterNumberCheck
    public JsonNode build(String patientId, LocalDate start, LocalDate end, String personal, String vardenhet, String vardgivare, String diagnos, int arbetsformaga) {
        return build(patientId, start, end, personal, vardenhet, vardgivare, diagnos, Arrays.asList(String.valueOf(arbetsformaga)));
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String personal, String vardenhet, String vardgivare, String diagnos, List <String> arbetsformaga) {
        return build(patientId, list(start), list(end), personal, vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    public JsonNode build(String person, List<LocalDate> starts, List<LocalDate> stops, String enhet, String vardgivare, String diagnos, List<String> grads) {
        return build(person, starts, stops, "personalId", enhet, vardgivare, diagnos, grads);
    }

    public JsonNode build(String person, List<LocalDate> starts, List<LocalDate> stops, String personal, String enhet, String vardgivare, String diagnos, List<String> grads) {
        ObjectNode intyg = template.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) intyg.path("patient").path("id");
        patientIdNode.put("extension", person);
        LocalDateTime startWithTime = starts.get(0).toLocalDateTime(SIGN_TIME_OF_DAY);
        intyg.put("signeringsdatum", ISO_FORMATTER.print(startWithTime));

        ObjectNode idNode = (ObjectNode) intyg.path("id");
        idNode.put("root", UUID.randomUUID().toString());

        for (JsonNode node: intyg.path("observationer")) {
            if (DocumentHelper.ARBETSFORMAGA_MATCHER.match(node)) {
                ObjectNode varde = (ObjectNode) node.path("observationsperiod");
                varde.put("from", FORMATTER.print(starts.get(0)));
                starts.remove(0);
                varde.put("tom", FORMATTER.print(stops.get(0)));
                stops.remove(0);
            }
        }

        ((ObjectNode) intyg.path("skapadAv").path("id")).put("extension", personal);
        ((ObjectNode) intyg.path("skapadAv").path("vardenhet").path("id")).put("extension", enhet);
        ((ObjectNode) intyg.path("skapadAv").path("vardenhet").path("vardgivare").path("id")).put("extension", vardgivare);
        for (JsonNode observation: intyg.path("observationer")) {
            if (DocumentHelper.DIAGNOS_MATCHER.match(observation)) {
                ((ObjectNode) observation.path("observationskod")).put("code", diagnos);
            }
        }

        int i = 0;
        for (JsonNode observation: intyg.path("observationer")) {
            if (DocumentHelper.ARBETSFORMAGA_MATCHER.match(observation)) {
                String a = "0";
                if (i < grads.size()) {
                    a = grads.get(i);
                    i++;
                }
                ((ObjectNode) observation.path("varde").path(0)).put("quantity", a);
            }
        }

        return intyg;
    }
    //CHECKSTYLE:ON ParameterNumberCheck

    private List<LocalDate> list(LocalDate date) {
        List<LocalDate> list = new ArrayList<>();
        list.add(date);
        return list;
    }

    private static String readTemplate(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(UtlatandeBuilder.class.getResourceAsStream(path), "utf8"))) {
            StringBuilder sb = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
