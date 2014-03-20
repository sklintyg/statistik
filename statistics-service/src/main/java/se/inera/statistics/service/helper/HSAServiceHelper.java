package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.model.Lan;

public final class HSAServiceHelper {

    private HSAServiceHelper() {
    }

    public static String getLan(JsonNode hsaData) {
        try {
            String result = hsaData.path("enhet").path("geografi").path("lan").textValue();
            return result != null ? result : Lan.OVRIGT;
        } catch (NullPointerException e) {
            return Lan.OVRIGT;
        }
    }
}
