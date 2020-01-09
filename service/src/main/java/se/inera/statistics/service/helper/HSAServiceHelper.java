/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.helper;

import com.google.common.base.Joiner;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.hsa.HsaInfoEnhet;
import se.inera.statistics.service.hsa.HsaInfoEnhetGeo;
import se.inera.statistics.service.hsa.HsaInfoPersonal;
import se.inera.statistics.service.hsa.HsaInfoVg;
import se.inera.statistics.service.report.model.Kommun;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.VerksamhetsTyp;
import se.inera.statistics.service.warehouse.WidelineConverter;
import se.inera.statistics.service.warehouse.query.LakarbefattningQuery;

public final class HSAServiceHelper {

    private static final Logger LOG = LoggerFactory.getLogger(HSAServiceHelper.class);

    private static Joiner joiner = Joiner.on(",").skipNulls();

    private HSAServiceHelper() {
    }

    public static String getHuvudEnhetId(HsaInfo hsaData) {
        if (hsaData == null) {
            return null;
        }
        return getEnhetId(hsaData.getHuvudenhet());
    }

    public static String getUnderenhetId(HsaInfo hsaData) {
        if (hsaData == null) {
            return null;
        }
        String result = getEnhetId(hsaData.getHuvudenhet());
        if (result != null) {
            return getEnhetId(hsaData.getEnhet());
        }
        return null;
    }

    public static HsaIdVardgivare getVardgivarId(HsaInfo hsaData) {
        if (hsaData == null) {
            return HsaIdVardgivare.empty();
        }
        final HsaInfoVg vardgivare = hsaData.getVardgivare();
        if (vardgivare == null) {
            return HsaIdVardgivare.empty();
        }
        return new HsaIdVardgivare(vardgivare.getId());
    }

    public static String getLan(HsaInfo hsaData) {
        if (hsaData != null) {
            String result = getLan(hsaData.getEnhet());
            if (result == null) {
                result = getLan(hsaData.getHuvudenhet());
            }
            try {
                return result != null && result.length() <= 2 && Integer.parseInt(result) >= 0 ? result : Lan.OVRIGT_ID;
            } catch (NumberFormatException e) {
                return Lan.OVRIGT_ID;
            }
        } else {
            return Lan.OVRIGT_ID;
        }
    }

    private static String getLan(HsaInfoEnhet hsaData) {
        if (hsaData == null) {
            return null;
        }
        final HsaInfoEnhetGeo geografi = hsaData.getGeografi();
        if (geografi == null) {
            return null;
        }
        return geografi.getLan();
    }

    public static String getKommun(HsaInfo hsaData) {
        if (hsaData != null) {
            String result = getKommun(hsaData.getEnhet());
            if (result == null) {
                result = getKommun(hsaData.getHuvudenhet());
            }
            try {
                final boolean isValidKommunId = result != null
                    && result.length() <= WidelineConverter.MAX_LENGTH_KOMMUN_ID
                    && Integer.parseInt(result) >= 0;
                return isValidKommunId ? result : Kommun.OVRIGT_ID.substring(2);
            } catch (NumberFormatException e) {
                return Kommun.OVRIGT_ID.substring(2);
            }
        } else {
            return Kommun.OVRIGT_ID.substring(2);
        }
    }

    private static String getKommun(HsaInfoEnhet hsaData) {
        if (hsaData == null) {
            return null;
        }
        final HsaInfoEnhetGeo geografi = hsaData.getGeografi();
        if (geografi == null) {
            return null;
        }
        return geografi.getKommun();
    }

    public static int getLakaralder(HsaInfo hsaData) {
        try {
            final String result = hsaData.getPersonal().getAlder();
            return result != null ? Integer.parseInt(result) : 0;
        } catch (NullPointerException | NumberFormatException e) {
            LOG.debug("Could not parse lakare age", e);
            return 0;
        }
    }

    public static int getLakarkon(HsaInfo hsaData) {
        try {
            final String result = hsaData.getPersonal().getKon();
            return result != null ? Integer.parseInt(result) : Kon.UNKNOWN.getNumberRepresentation();
        } catch (NullPointerException | NumberFormatException e) {
            LOG.debug("Could not parse lakare gender", e);
            return Kon.UNKNOWN.getNumberRepresentation();
        }
    }

    public static String getLakarbefattning(HsaInfo hsaData) {
        final HsaInfoPersonal personal = getHsaInfoPersonalNullSafe(hsaData);
        if (personal == null) {
            return String.valueOf(LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE);
        }
        final List<String> befattning = personal.getBefattning();
        if (befattning == null || befattning.isEmpty()) {
            return String.valueOf(LakarbefattningQuery.UNKNOWN_BEFATTNING_CODE);
        }
        return joiner.join(befattning);
    }

    private static HsaInfoPersonal getHsaInfoPersonalNullSafe(HsaInfo hsaData) {
        if (hsaData == null) {
            return null;
        }
        return hsaData.getPersonal();
    }

    public static String getVerksamhetsTyper(HsaInfo hsaData, boolean forceHuvudenhet) {
        if (hsaData == null) {
            return VerksamhetsTyp.OVRIGT_ID;
        }
        String result = getVerksamhetsTyper(hsaData.getEnhet());
        if (forceHuvudenhet || result == null) {
            result = getVerksamhetsTyper(hsaData.getHuvudenhet());
        }
        final boolean isVardcentral = (!forceHuvudenhet && isVardcentral(hsaData.getEnhet())) || isVardcentral(hsaData.getHuvudenhet());
        result = isVardcentral ? (result != null && !result.isEmpty() ? result + "," : "") + VerksamhetsTyp.VARDCENTRAL_ID : result;
        return result != null && !result.isEmpty() ? result : VerksamhetsTyp.OVRIGT_ID;
    }

    private static boolean isVardcentral(HsaInfoEnhet hsaData) {
        final List<String> enhetstyper = getEnhetstyper(hsaData);
        for (String enhetstyp : enhetstyper) {
            if (VerksamhetsTyp.VARDCENTRAL_ID.equals(enhetstyp)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getEnhetstyper(HsaInfoEnhet hsaData) {
        if (hsaData == null) {
            return Collections.emptyList();
        }
        final List<String> enhetsTyps = hsaData.getEnhetsTyp();
        if (enhetsTyps == null) {
            return Collections.emptyList();
        }
        return enhetsTyps;
    }

    private static String getVerksamhetsTyper(HsaInfoEnhet hsaData) {
        if (hsaData == null) {
            return null;
        }
        final List<String> verksamhets = hsaData.getVerksamhet();
        if (verksamhets == null) {
            return null;
        }
        return joiner.join(verksamhets);
    }

    private static String getEnhetId(HsaInfoEnhet hsaData) {
        if (hsaData == null) {
            return null;
        }
        return hsaData.getId();
    }

    public static HsaIdLakare getLakareId(HsaInfo hsaData) {
        final HsaInfoPersonal personal = getHsaInfoPersonalNullSafe(hsaData);
        if (personal == null) {
            return null;
        }
        return new HsaIdLakare(personal.getId());
    }

    public static String getLakareTilltalsnamn(HsaInfo hsaData) {
        final HsaInfoPersonal personal = getHsaInfoPersonalNullSafe(hsaData);
        if (personal == null) {
            return "";
        }
        final String result = personal.getTilltalsnamn();
        return result != null ? result : "";
    }

    public static String getLakareEfternamn(HsaInfo hsaData) {
        final HsaInfoPersonal personal = getHsaInfoPersonalNullSafe(hsaData);
        if (personal == null) {
            return "";
        }
        final String result = personal.getEfternamn();
        return result != null ? result : "";
    }

}
