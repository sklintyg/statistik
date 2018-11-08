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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum IntygType {

    AF00213("af00213", "AF00213", "AF00213 Arbetsförmedlingens medicinska utlåtande", true),
    DB("db", "Dödsbevis", "Dödsbevis", true),
    DOI("doi", "Dödsorsaksintyg", "Dödsorsaksintyg", true),
    SJUKPENNING(null, "FK 7263/7804", "FK 7263/7804 Läkarintyg för sjukpenning", false),
    FK7263("fk7263", "FK 7263", "FK 7263 Läkarintyg", true),
    LISJP("lisjp", "FK 7804", "FK 7804 Läkarintyg för sjukpenning", true),
    LUSE("luse", "FK 7800", "FK 7800 Läkarutlåtande för sjukersättning", true),
    LUAE_NA("luae_na", "FK 7801", "FK 7801 Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga", true),
    LUAE_FS("luae_fs", "FK 7802", "FK 7802 Läkarutlåtande för aktivitetsersättning vid förlängd skolgång", true),
    UNKNOWN(null, "Okänt", "Okänt", false),
    TSTRK1007("ts-bas", "TSTRK1007", "TSTRK1007 Transportstyrelsens läkarintyg", true),
    TSTRK1031("ts-diabetes", "TSTRK1031", "TSTRK1031 Transportstyrelsens läkarintyg diabetes", true),
    AG114("ag114", "AG1-14", "AG1-14 Läkarintyg om arbetsförmåga - sjuklöneperioden", true);

    private static final Set<IntygType> INCLUDED_IN_KOMPLETTERING_REPORT = Collections.unmodifiableSet(
            Stream.of(LISJP, LUSE, LUAE_NA, LUAE_FS).collect(Collectors.toSet()));

    private static final Set<IntygType> IS_SJUKPENNING = Collections.unmodifiableSet(
            Stream.of(LISJP, FK7263).collect(Collectors.toSet()));

    private static final List<IntygType> INCLUDED_IN_INTYG_TOTALT_REPORT = Collections.unmodifiableList(
            Arrays.asList(AG114, AF00213, DB, DOI, LISJP, LUSE, LUAE_NA, LUAE_FS, TSTRK1007, TSTRK1031));

    private static final List<IntygType> INCLUDED_IN_INTYG_FILTER = Collections.unmodifiableList(
            Arrays.asList(SJUKPENNING, LUSE, LUAE_NA, LUAE_FS));


    private final String itIntygType; //The type name Intygtjansten is using and sends as metadata with all intyg
    private final String text;
    private final String shortText;
    private final boolean isSupportedIntyg;

    IntygType(String itIntygType, String shortText, String text, boolean isSupportedIntyg) {
        this.itIntygType = itIntygType;
        this.text = text;
        this.shortText = shortText;
        this.isSupportedIntyg = isSupportedIntyg;
    }

    public String getItIntygType() {
        return itIntygType;
    }

    public String getText() {
        return text;
    }

    public String getShortText() {
        return shortText;
    }

    public boolean isSupportedIntyg() {
        return isSupportedIntyg;
    }

    public boolean isSjukpenningintyg() {
        return IS_SJUKPENNING.contains(this);
    }

    public boolean isIncludedInKompletteringReport() {
        return INCLUDED_IN_KOMPLETTERING_REPORT.contains(this);
    }

    public Collection<IntygType> getUnmappedTypes() {
        if (SJUKPENNING.equals(this)) {
            return Arrays.asList(IntygType.FK7263, IntygType.LISJP);
        }
        return Collections.singleton(this);
    }

    public static Optional<IntygType> getByName(String name) {
        return Arrays.stream(values()).filter(group -> group.text.equalsIgnoreCase(name)).findFirst();
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

    public static List<IntygType> getInIntygtypFilter() {
        return INCLUDED_IN_INTYG_FILTER;
    }

    public static List<IntygType> getInIntygtypTotal() {
        return INCLUDED_IN_INTYG_TOTALT_REPORT;
    }

}
