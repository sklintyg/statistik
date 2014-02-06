package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.model.Lan;

public final class HSAServiceHelper {

    private HSAServiceHelper() {
    }

    public static String getLan(JsonNode hsaData) {
        if (hsaData != null) {
            String result = hsaData.path("geografi").path("lan").path("kod").textValue();
            return result != null ? result : Lan.OVRIGT;
        } else {
            return Lan.OVRIGT;
        }
    }
}
