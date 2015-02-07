/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;

import java.util.Iterator;

public final class HSAServiceHelper {

    private static Joiner joiner = Joiner.on(",").skipNulls();

    private HSAServiceHelper() {
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

    public static String getEnhetNamn(JsonNode hsaData) {
        if (hsaData == null) {
            return null;
        }
        String result = getEnhetNamn(hsaData, "huvudenhet");
        if (result == null) {
            result = getEnhetNamn(hsaData, "enhet");
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

    public static String getVardgivarNamn(JsonNode hsaData) {
        if (hsaData == null) {
            return null;
        } else {
            return hsaData.path("vardgivare").path("namn").textValue();
        }
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

    private static String getLan(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("geografi").path("lan").textValue();
    }

    public static String getKommun(JsonNode hsaData) {
        if (hsaData != null) {
            String result = getKommun(hsaData, "enhet");
            if (result == null) {
                result = getKommun(hsaData, "huvudenhet");
            }
            return result != null ? result : Kommun.OVRIGT_ID;
        } else {
            return Kommun.OVRIGT_ID;
        }
    }

    private static String getKommun(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("geografi").path("kommun").textValue();
    }

    public static int getLakaralder(JsonNode hsaData) {
        try {
            String result = hsaData.path("personal").path("alder").textValue();
            return result != null ? Integer.parseInt(result) : 0;
        } catch (NullPointerException | NumberFormatException e) {
            return 0;
        }
    }

    public static int getLakarkon(JsonNode hsaData) {
        try {
            String result = hsaData.path("personal").path("kon").textValue();
            return result != null ? Integer.parseInt(result) : 0;
        } catch (NullPointerException | NumberFormatException e) {
            return 0;
        }
    }

    public static String getLakarbefattning(JsonNode hsaData) {
        Iterator<String> stringIterator = Iterators.transform(hsaData.path("personal").path("befattning").elements(),
                new Function<JsonNode, String>() {
                    @Override
                    public String apply(JsonNode node) {
                        return node.textValue();
                    }
                });
        return joiner.join(stringIterator);
    }

    public static String getVerksamhetsTyper(JsonNode hsaData) {
        if (hsaData != null) {
            String result = getVerksamhetsTyper(hsaData, "enhet");
            if (result == null) {
                result = getVerksamhetsTyper(hsaData, "huvudenhet");
            }
            final boolean isVardcentral = isVardcentral(hsaData, "enhet") || isVardcentral(hsaData, "huvudenhet");
            result = isVardcentral ? (result != null && !result.isEmpty() ? result + ",02" : "02") : result;
            return result != null && !result.isEmpty() ? result : VerksamhetsTyp.OVRIGT_ID;
        } else {
            return VerksamhetsTyp.OVRIGT_ID;
        }
    }

    private static boolean isVardcentral(JsonNode hsaData, String enhet) {
        final Iterator<String> enhetstyper = getEnhetstyper(hsaData, enhet);
        while (enhetstyper.hasNext()) {
            String enhetstyp = enhetstyper.next();
            if ("02".equals(enhetstyp)) {
                return true;
            }
        }
        return false;
    }

    public static Iterator<String> getEnhetstyper(JsonNode hsaData, String enhet) {
        return Iterators.transform(hsaData.path(enhet).path("enhetstyp").elements(),
        new Function<JsonNode, String>() {
                    @Override
                    public String apply(JsonNode node) {
                        return node.asText();
                    }
                });
    }

    private static String getVerksamhetsTyper(JsonNode hsaData, String enhet) {
        Iterator<String> stringIterator = Iterators.transform(hsaData.path(enhet).path("verksamhet").elements(),
                new Function<JsonNode, String>() {
                    @Override
                    public String apply(JsonNode node) {
                        return node.asText();
                    }
                });
        return joiner.join(stringIterator);
    }

    private static String getEnhetId(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("id").textValue();
    }

    private static String getEnhetNamn(JsonNode hsaData, String enhet) {
        return hsaData.path(enhet).path("namn").textValue();
    }

    public static String getLakareId(JsonNode hsaData) {
        String result = hsaData.path("personal").path("id").textValue();
        return result != null ? result : "";
    }

    public static String getLakareTilltalsnamn(JsonNode hsaData) {
        String result = hsaData.path("personal").path("tilltalsnamn").textValue();
        return result != null ? result : "";
    }

    public static String getLakareEfternamn(JsonNode hsaData) {
        String result = hsaData.path("personal").path("efternamn").textValue();
        return result != null ? result : "";
    }

}
