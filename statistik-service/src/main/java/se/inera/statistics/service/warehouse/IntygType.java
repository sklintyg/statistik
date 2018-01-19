/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public enum IntygType {

    FK7263("fk7263", "FK 7263 Läkarintyg", true, true, false),
    LISU(null, "LISU", true, true, false),
    LUSE("luse", "FK 7800 Läkarutlåtande för sjukersättning", true, false, true),
    LUAE_NA("luae_na", "FK 7801 Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga", true, false, true),
    LUAE_FS("luae_fs", "FK 7802 Läkarutlåtande för aktivitetsersättning vid förlängd skolgång", true, false, true),
    LISJP("lisjp", "FK 7804 Läkarintyg för sjukpenning", true, true, false),
    UNKNOWN(null, "Okänt", false, false, false),
    SJUKPENNING(null, "FK 7263/7804 Läkarintyg för sjukpenning", false, false, false);

    private final String itIntygType; //The type name Intygtjansten is using and sends as metadata with all intyg
    private final String text;
    private final boolean isSupportedIntyg;
    private final boolean isSjukpenningintyg;
    private final boolean includeInIntygtypFilter;

    IntygType(String itIntygType, String text, boolean isSupportedIntyg,
              boolean isSjukpenningintyg, boolean includeInIntygtypFilter) {
        this.itIntygType = itIntygType;
        this.text = text;
        this.isSupportedIntyg = isSupportedIntyg;
        this.isSjukpenningintyg = isSjukpenningintyg;
        this.includeInIntygtypFilter = includeInIntygtypFilter;
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

    public boolean isIncludeInIntygtypFilter() {
        return includeInIntygtypFilter;
    }

    public static IntygType parseString(String stringType) {
        for (IntygType intygType : values()) {
            if (intygType.name().equalsIgnoreCase(stringType)) {
                return intygType;
            }
        }
        return UNKNOWN;
    }

    public static Optional<IntygType> parseStringOptional(String stringType) {
        for (IntygType intygType : values()) {
            if (intygType.name().equalsIgnoreCase(stringType)) {
                return Optional.of(intygType);
            }
        }
        return Optional.empty();
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

    public IntygType getMappedType() {
        if (IntygType.FK7263.equals(this) || IntygType.LISJP.equals(this)) {
            return SJUKPENNING;
        }
        return this;
    }

    public Collection<IntygType> getUnmappedTypes() {
        if (SJUKPENNING.equals(this)) {
            return Arrays.asList(IntygType.FK7263, IntygType.LISJP);
        }
        return Collections.singleton(this);
    }

}
