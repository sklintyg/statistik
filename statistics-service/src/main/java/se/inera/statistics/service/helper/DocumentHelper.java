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

}
