/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

public enum IntygType {

    FK7263("fk7263", "FK 7263 Läkarintyg", true, true),
    LISU(null, "LISU", true, true),
    LUSE("luse", "FK 7800 Läkarutlåtande för sjukersättning", true, false),
    LUAE_NA("luae_na", "FK 7801 Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga", true, false),
    LUAE_FS("luae_fs", "FK 7802 Läkarutlåtande för aktivitetsersättning vid förlängd skolgång", true, false),
    LISJP("lisjp", "FK 7804 Läkarintyg för sjukpenning", true, true),
    UNKNOWN(null, "Okänt", false, false);

    private final String itIntygType; //The type name Intygtjansten is using and sends as metadata with all intyg
    private final String text;
    private final boolean isSupportedIntyg;
    private final boolean isSjukpenningintyg;

    IntygType(String itIntygType, String text, boolean isSupportedIntyg, boolean isSjukpenningintyg) {
        this.itIntygType = itIntygType;
        this.text = text;
        this.isSupportedIntyg = isSupportedIntyg;
        this.isSjukpenningintyg = isSjukpenningintyg;
    }

    public String getItIntygType() {
        return itIntygType;
    }

    public String getText() {
        return text;
    }

    public boolean isSupportedIntyg() {
        return isSupportedIntyg;
    }

    public boolean isSjukpenningintyg() {
        return isSjukpenningintyg;
    }

    public static IntygType parseString(String stringType) {
        for (IntygType intygType : values()) {
            if (intygType.name().equalsIgnoreCase(stringType)) {
                return intygType;
            }
        }
        return UNKNOWN;
    }

    public static IntygType getByItIntygType(String itIntygType) {
        if (itIntygType == null) {
            return UNKNOWN;
        }
        for (IntygType intygType : values()) {
            if (itIntygType.equalsIgnoreCase(intygType.itIntygType)) {
                return intygType;
            }
        }
        return UNKNOWN;
    }

}
