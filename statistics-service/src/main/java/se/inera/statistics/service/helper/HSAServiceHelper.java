package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.model.Lan;

public final class HSAServiceHelper {

    private HSAServiceHelper() {
    }

    public static String getLan(JsonNode hsaData) {
        try {
            String result = getLan(hsaData, "enhet");
            if (result == null) {
                result = getLan(hsaData, "huvudenhet");
            }
            return result != null ? result : Lan.OVRIGT;
        } catch (NullPointerException e) {
            return Lan.OVRIGT;
        }
    }

    public static String getEnhetId(JsonNode hsaData) {
        if (hsaData == null) {
            return null;
        }
        String result = getEnhetId(hsaData, "huvudenhet");
        if (result == null) {
            result = getEnhetId(hsaData, "enhet");
        }
        return result;
    }

    public static String getVardgivarId(JsonNode hsaData) {
        if (hsaData == null) {
            return null;
        } else {
            return hsaData.path("vardgivare").path("id").textValue();
        }
    }

    private static String getLan(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("geografi").path("lan").textValue();
    }

    private static String getEnhetId(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("id").textValue();
    }

}
