package se.inera.statistics.service.helper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public final class DocumentHelper {

    public static final Matcher DIAGNOS_MATCHER = Matcher.Builder.matcher("observationsKategori").add(Matcher.Builder.matcher("code", "439401001")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));
    public static final Matcher ARBETSFORMAGA_MATCHER = Matcher.Builder.matcher("observationsKod").add(Matcher.Builder.matcher("code", "302119000")).add((Matcher.Builder.matcher("codeSystem", "1.2.752.116.2.1.1.1")));

    private DocumentHelper() {
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
        return document.path("validFromDate").textValue();
    }

    public static String getSistaNedsattningsdag(JsonNode document) {
        return document.path("validToDate").textValue();
    }

    public static String getKon(JsonNode document) {
        return document.path("patient").path("kon").textValue();
    }

    public static String getDiagnos(JsonNode document) {
        for (JsonNode node: document.path("observations")) {
            if (DIAGNOS_MATCHER.match(node)) {
                return node.path("observationsKod").path("code").textValue();
            }
        }
        return null;
    }

    public static List<String> getArbetsformaga(JsonNode document) {
        List<String> result = new ArrayList<>();
        for (JsonNode node: document.path("observations")) {
            if (ARBETSFORMAGA_MATCHER.match(node)) {
                for (JsonNode varde: node.path("varde")) {
                    result.add(varde.path("quantity").asText());
                }
            }
        }
        return result;
    }
}
