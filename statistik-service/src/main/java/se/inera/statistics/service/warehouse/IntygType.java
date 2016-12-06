/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

    FK7263(true, true),
    LIS(true, true),
    LISU(true, true),
    LUSE(true, false),
    LUAE_NA(true, false),
    LUAE_FS(true, false),
    LISJP(true, true),
    UNKNOWN(false, false);

    private final boolean isSupportedIntyg;
    private final boolean isSjukpenningintyg;

    IntygType(boolean isSupportedIntyg, boolean isSjukpenningintyg) {
        this.isSupportedIntyg = isSupportedIntyg;
        this.isSjukpenningintyg = isSjukpenningintyg;
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
