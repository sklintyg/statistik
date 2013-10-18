package se.inera.statistics.service.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UtlatandeBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(UtlatandeBuilder.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    private final JsonNode template;

    public UtlatandeBuilder() {
        this(readTemplate("/json/fk7263_M_template.json"));
    }

    public UtlatandeBuilder(String templateText) {
        template = JSONParser.parse(templateText);
    }

    public JsonNode build(String patientId, LocalDate start, LocalDate end, String vardenhet, String diagnos, int arbetsformaga) {
        ObjectNode intyg = template.deepCopy();
        ObjectNode patientIdNode = (ObjectNode) intyg.path("patient").path("id");
        patientIdNode.put("extension", patientId);

        intyg.put("validFromDate", FORMATTER.print(start));
        intyg.put("validToDate", FORMATTER.print(end));

        ((ObjectNode) intyg.path("skapadAv").path("vardenhet").path("id")).put("extension", vardenhet);
        for (JsonNode observation: intyg.path("observations")) {
            if (DocumentHelper.DIAGNOS_MATCHER.match(observation)) {
                ((ObjectNode) observation.path("observationsKod")).put("code", diagnos);
            }
        }

        for (JsonNode observation: intyg.path("observations")) {
            if (DocumentHelper.ARBETSFORMAGA_MATCHER.match(observation)) {
                ((ObjectNode) observation.path("varde").path(0)).put("quantity", arbetsformaga);
            }
        }

        LOG.info("New permutation" + intyg.toString());
        return intyg;
    }

    private static String readTemplate(String path) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(UtlatandeBuilder.class.getResourceAsStream(path), "utf8"));) {
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
