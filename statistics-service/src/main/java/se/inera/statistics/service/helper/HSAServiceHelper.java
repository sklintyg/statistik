/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import se.inera.statistics.service.report.model.Lan;

public final class HSAServiceHelper {

    private HSAServiceHelper() {
    }

    public static String getLan(JsonNode hsaData) {
        if (hsaData != null) {
            String result = getLan(hsaData, "enhet");
            if (result == null) {
                result = getLan(hsaData, "huvudenhet");
            }
            return result != null ? result : Lan.OVRIGT_ID;
        } else {
            return Lan.OVRIGT_ID;
        }
    }

    public static String getKommun(JsonNode hsaData) {
        String result = hsaData.path("geografi").path("kommun").path("kod").textValue();
        return result != null ? result : "";
    }

    public static int getLakaralder(JsonNode hsaData) {
        try {
            String result = hsaData.path("personal").path("alder").textValue();
            return result != null ? Integer.parseInt(result) : 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static int getLakarkon(JsonNode hsaData) {
        try {
            String result = hsaData.path("personal").path("kon").textValue();
            return result != null ? Integer.parseInt(result) : 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }
    public static String getLakarbefattning(JsonNode hsaData) {
        String result = hsaData.path("personal").path("befattning").textValue();
        return result != null ? result : "";
    }

    private static String getLan(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("geografi").path("lan").textValue();
    }
}
