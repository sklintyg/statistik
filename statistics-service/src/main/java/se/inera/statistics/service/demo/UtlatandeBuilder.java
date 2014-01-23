package se.inera.statistics.service.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UtlatandeBuilder {
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    private final JsonNode template;
    private String testName;

    public UtlatandeBuilder() {
        this(readTemplate("/json/fk7263_M_template.json"));
    }

    public UtlatandeBuilder(String templateText) {
        template = JSONParser.parse(templateText);
    }

    public UtlatandeBuilder(String templateName, String testName) {
        this.testName = testName;
        template = JSONParser.parse(readTemplate(templateName));
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

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String personal, String vardenhet, String vardgivare, String diagnos, int arbetsformaga) {
        return build(patientId, start, end, personal, vardenhet, vardgivare, diagnos, Arrays.asList(String.valueOf(arbetsformaga)));
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String personal, String vardenhet, String vardgivare, String diagnos, List <String> arbetsformaga) {
        ObjectNode intyg = template.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) intyg.path("patient").path("id");
        patientIdNode.put("extension", patientId);

        ObjectNode idNode = (ObjectNode) intyg.path("id");
        idNode.put("root", UUID.randomUUID().toString());

        intyg.put("validFromDate", FORMATTER.print(start));
        intyg.put("validToDate", FORMATTER.print(end));

        for (JsonNode node: intyg.path("observationer")) {
            if (DocumentHelper.ARBETSFORMAGA_MATCHER.match(node)) {
                ObjectNode varde = (ObjectNode) node.path("observationsperiod");
                varde.put("from", FORMATTER.print(start));
                varde.put("tom", FORMATTER.print(end));
            }
        }

        ((ObjectNode) intyg.path("skapadAv").path("id")).put("extension", personal);
        ((ObjectNode) intyg.path("skapadAv").path("vardenhet").path("id")).put("extension", vardenhet);
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
                if (i < arbetsformaga.size()) {
                    a = arbetsformaga.get(i);
                    i++;
                }
                ((ObjectNode) observation.path("varde").path(0)).put("quantity", a);
            }
        }

        return intyg;
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

    public String getTestName() {
        return testName;
    }

    public JsonNode build(String person, List<LocalDate> starts, List<LocalDate> stops, String enhet, String vardgivare, String diagnos, List<String> grads) {
        ObjectNode intyg = template.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) intyg.path("patient").path("id");
        patientIdNode.put("extension", person);

        ObjectNode idNode = (ObjectNode) intyg.path("id");
        idNode.put("root", UUID.randomUUID().toString());

        LocalDate start = new LocalDate("9999-12-31");
        for (LocalDate s : starts) {
            if (s.isBefore(start)) {
                start = s;
            }
        }
        LocalDate end = new LocalDate("1970-01-01");
        for (LocalDate e : stops) {
            if (e.isAfter(end)) {
                end = e;
            }
        }

        intyg.put("validFromDate", FORMATTER.print(start));
        intyg.put("validToDate", FORMATTER.print(end));

        for (JsonNode node: intyg.path("observationer")) {
            if (DocumentHelper.ARBETSFORMAGA_MATCHER.match(node)) {
                ObjectNode varde = (ObjectNode) node.path("observationsperiod");
                varde.put("from", FORMATTER.print(starts.get(0)));
                starts.remove(0);
                varde.put("tom", FORMATTER.print(stops.get(0)));
                stops.remove(0);
            }
        }

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
}
