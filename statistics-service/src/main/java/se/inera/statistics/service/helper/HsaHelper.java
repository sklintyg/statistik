package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.util.Lan;

public final class HsaHelper {

    private HsaHelper() {
    }

    public static String getLan(JsonNode hsaData) {
        try {
            String result = hsaData.path("geografi").path("lan").path("kod").textValue();
            return result != null ? result : Lan.OVRIGT;
        } catch (NullPointerException e) {
            return Lan.OVRIGT;
        }
    }
}
