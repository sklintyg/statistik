/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.testsupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.JSONParser;

public class UtlatandeBuilder {

    private static final LocalTime SIGN_TIME_OF_DAY = LocalTime.of(7, 7);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss.SSS");
    public static final String GRUND_DATA = "grundData";
    public static final String SKAPAD_AV = "skapadAv";

    private final JsonNode template;

    public UtlatandeBuilder() {
        this("/json/fk7263-internal.json");
    }

    public UtlatandeBuilder(String templateFile) {
        template = JSONParser.parse(readTemplate(templateFile));
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, HsaIdEnhet vardenhet, String diagnos, int arbetsformaga) {
        return build(patientId, start, end, vardenhet, new HsaIdVardgivare("vardgivarId"), diagnos, arbetsformaga);
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, HsaIdEnhet vardenhet, HsaIdVardgivare vardgivare,
        String diagnos, int arbetsformaga) {
        return build(patientId, start, end, new HsaIdLakare("Personal HSA-ID"), vardenhet, vardgivare, diagnos,
            Collections.singletonList(String.valueOf(arbetsformaga)));
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, HsaIdEnhet vardenhet, HsaIdVardgivare vardgivare,
        String diagnos, List<String> arbetsformaga) {
        return build(patientId, start, end, new HsaIdLakare("Personal HSA-ID"), vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Parameter number check ignored in Sonar
    public JsonNode build(String patientId, LocalDate start, LocalDate end, HsaIdLakare personal, HsaIdEnhet vardenhet,
        HsaIdVardgivare vardgivare, String diagnos, int arbetsformaga) {
        return build(patientId, start, end, personal, vardenhet, vardgivare, diagnos,
            Collections.singletonList(String.valueOf(arbetsformaga)));
    }

    @java.lang.SuppressWarnings("squid:S00107") // Parameter number check ignored in Sonar
    public JsonNode build(String patientId, LocalDate start, LocalDate end, HsaIdLakare personal, HsaIdEnhet vardenhet,
        HsaIdVardgivare vardgivare, String diagnos, List<String> arbetsformaga) {
        return build(patientId, list(start), list(end), personal, vardenhet, vardgivare, diagnos, arbetsformaga);
    }

    public JsonNode build(String person, List<LocalDate> starts, List<LocalDate> stops, HsaIdEnhet enhet, HsaIdVardgivare vardgivare,
        String diagnos, List<String> grads) {
        return build(person, starts, stops, new HsaIdLakare("personalId"), enhet, vardgivare, diagnos, grads);
    }

    @java.lang.SuppressWarnings("squid:S00107") // Parameter number check ignored in Sonar
    public JsonNode build(String person, List<LocalDate> starts, List<LocalDate> stops, HsaIdLakare personal, HsaIdEnhet enhet,
        HsaIdVardgivare vardgivare, String diagnos, List<String> grads) {
        ObjectNode intyg = template.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) intyg.path(GRUND_DATA).path("patient");
        patientIdNode.put("personId", person);
        LocalDateTime startWithTime = starts.get(0).atTime(SIGN_TIME_OF_DAY);
        ObjectNode signeringsdatumNode = (ObjectNode) intyg.path(GRUND_DATA);
        signeringsdatumNode.put("signeringsdatum", ISO_FORMATTER.format(startWithTime));

        intyg.put("id", UUID.randomUUID().toString());

        for (String grad : grads) {
            addGrad(starts, stops, intyg, grad);
        }

        ((ObjectNode) intyg.path(GRUND_DATA).path(SKAPAD_AV)).put("personId", personal.getId());
        ((ObjectNode) intyg.path(GRUND_DATA).path(SKAPAD_AV).path("vardenhet")).put("enhetsid", enhet.getId());
        ((ObjectNode) intyg.path(GRUND_DATA).path(SKAPAD_AV).path("vardenhet").path("vardgivare")).put("vardgivarid", vardgivare.getId());
        intyg.put("diagnosKod", diagnos);

        return intyg;
    }

    private void addGrad(List<LocalDate> starts, List<LocalDate> stops, ObjectNode intyg, String grad) {
        switch (grad) {
            case "0":
                createNedsatNode(starts, stops, intyg, "nedsattMed100");
                break;
            case "25":
                createNedsatNode(starts, stops, intyg, "nedsattMed75");
                break;
            case "50":
                createNedsatNode(starts, stops, intyg, "nedsattMed50");
                break;
            case "75":
                createNedsatNode(starts, stops, intyg, "nedsattMed25");
                break;
            default:
                throw new IllegalStateException("Unrecognized sjukskrivningsgrad:  " + grad);
        }
    }

    private void createNedsatNode(List<LocalDate> starts, List<LocalDate> stops, ObjectNode intyg, String nedsattning) {
        ObjectNode nedsatt = (ObjectNode) intyg.set(nedsattning, intyg.objectNode());
        ((ObjectNode) nedsatt.path(nedsattning)).put("from", FORMATTER.format(starts.get(0)));
        ((ObjectNode) nedsatt.path(nedsattning)).put("tom", FORMATTER.format(stops.get(0)));
        starts.remove(0);
        stops.remove(0);
    }
    // CHECKSTYLE:ON ParameterNumberCheck

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
            throw new ReadTemplateException(e);
        }
    }

    private static class ReadTemplateException extends RuntimeException {

        ReadTemplateException(Exception e) {
            super(e);
        }
    }

}
