/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

public enum IntygFormat {

    /**
     * Legacy format using JSON.
     */
    REGISTER_MEDICAL_CERTIFICATE(0),

    /**
     * New format using XML.
     */
    REGISTER_CERTIFICATE(1),

    REGISTER_TS_BAS(2),

    REGISTER_TS_DIABETES(3);

    private final int intValue;

    IntygFormat(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public static IntygFormat parseIntValue(int format) {
        for (IntygFormat intygFormat : values()) {
            if (intygFormat.intValue == format) {
                return intygFormat;
            }
        }
        throw new IllegalArgumentException("Format with value could not be found: " + format);
    }
}
