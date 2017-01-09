/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

public enum IntygType {

    FK7263("FK 7263 Läkarintyg", true, true),
    LISU("LISU", true, true),
    LUSE("FK 7800 Läkarutlåtande för sjukersättning", true, false),
    LUAE_NA("FK 7801 Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga", true, false),
    LUAE_FS("FK 7802 Läkarutlåtande för aktivitetsersättning vid förlängd skolgång", true, false),
    LISJP("FK 7804 Läkarintyg för sjukpenning", true, true),
    UNKNOWN("Okänt", false, false);

    private final String text;
    private final boolean isSupportedIntyg;
    private final boolean isSjukpenningintyg;

    IntygType(String text, boolean isSupportedIntyg, boolean isSjukpenningintyg) {
        this.text = text;
        this.isSupportedIntyg = isSupportedIntyg;
        this.isSjukpenningintyg = isSjukpenningintyg;
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

}
