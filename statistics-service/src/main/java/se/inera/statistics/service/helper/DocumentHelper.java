package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;

public final class DocumentHelper {

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
            if (isDiagnosObservation(node)) {
                return node.path("observationsKod").path("code").textValue();
            }
        }
        return null;
    }

    private static boolean isDiagnosObservation(JsonNode node) {
        JsonNode kategori = node.path("observationsKategori");
        return "439401001".equals(kategori.path("code").textValue()) && "1.2.752.116.2.1.1.1".equals(kategori.path("codeSystem").textValue()); 
    }

}
