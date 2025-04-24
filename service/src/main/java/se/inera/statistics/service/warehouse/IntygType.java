/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum IntygType {

    AF00213("AF00213", "af00213", "AF00213", "AF00213 Arbetsförmedlingens medicinska utlåtande", true),
    AF00251("AF00251", "af00251", "AF00251", "AF00251 Läkarintyg för deltagare i arbetsmarknadspolitiska program "
        + "med aktivitetsstöd, utvecklingsersättning eller etableringsersättning", true),
    DB("DB", "db", "Dödsbevis", "Dödsbevis", true),
    DOI("DOI", "doi", "Dödsorsaksintyg", "Dödsorsaksintyg", true),
    SJUKPENNING(null, null, "FK 7263/7804", "FK 7263/7804 Läkarintyg för sjukpenning", false),
    FK7263("FK7263", "fk7263", "FK 7263", "FK 7263 Läkarintyg", true),
    LISJP("LISJP", "lisjp", "FK 7804", "FK 7804 Läkarintyg för sjukpenning", true),
    LUSE("LUSE", "luse", "FK 7800", "FK 7800 Läkarutlåtande för sjukersättning", true),
    LUAE_NA("LUAE_NA", "luae_na", "FK 7801", "FK 7801 Läkarutlåtande för aktivitetsersättning vid nedsatt arbetsförmåga", true),
    LUAE_FS("LUAE_FS", "luae_fs", "FK 7802", "FK 7802 Läkarutlåtande för aktivitetsersättning vid förlängd skolgång", true),
    UNKNOWN(null, null, "Okänt", "Okänt", false),
    TSTRK1007("TSTRK1007", "ts-bas", "TSTRK1007", "TSTRK1007 Transportstyrelsens läkarintyg", true),
    TSTRK1031("TSTRK1031", "ts-diabetes", "TSTRK1031", "TSTRK1031 Transportstyrelsens läkarintyg diabetes", true),
    TSTRK1009("TSTRK1009", "tstrk1009", "TSTRK1009", "TSTRK1009 Transportstyrelsen Läkares anmälan", true),
    TSTRK1062("TSTRK1062", "tstrk1062", "TSTRK1062", "TSTRK1062 Transportstyrelsens läkarintyg ADHD", true),
    AG114("AG1-14", "ag114", "AG1-14", "AG1-14 Läkarintyg om arbetsförmåga - sjuklöneperioden", true),
    AG7804("AG7804", "ag7804", "AG7804", "AG7804 Läkarintyg om arbetsförmåga - arbetsgivare", true),
    FK7210("IGRAV", "fk7210", "FK 7210", "FK 7210 Intyg om graviditet", true),
    FK7426("LU_TFP_ASB18", "fk7426", "FK 7426",
        "FK 7426 Läkarutlåtande tillfällig föräldrapenning för ett allvarligt sjukt barn som inte har fyllt 18", true),
    FK7427("LU_TFP_B12_16", "fk7427", "FK 7427", "FK 7427 Läkarutlåtande tillfällig föräldrapenning barn 12–16 år", true),
    FK7472("ITFP", "fk7472", "FK 7472", "FK 7472 Intyg om tillfällig föräldrapenning", true),
    FK3226("LUNSP", "fk3226", "FK 3226", "FK 3226 Läkarutlåtande för närståendepenning", true),
    FK7809("LUMEK", "fk7809", "FK 7809", "FK 7809 Läkarutlåtande för merkostnadsersättning", true),
    TS8071("TS8071", "ts8071", "TS8071",
        "TS8071 Läkarintyg för högre körkortsbehörigheter, taxiförarlegitimation och på begäran av Transportstyrelsen", true);

    private static final ImmutableSet<IntygType> INCLUDED_IN_KOMPLETTERING_REPORT = ImmutableSet.of(LISJP, LUSE, LUAE_NA, LUAE_FS,
        FK7472, FK3226, FK7809, FK7427, FK7426);

    private static final ImmutableSet<IntygType> IS_SJUKPENNING = ImmutableSet.of(LISJP, FK7263);

    private static final ImmutableList<IntygType> INCLUDED_IN_INTYG_TOTALT_REPORT = ImmutableList.of(
        AF00213, AF00251, AG114, AG7804, DB, DOI, LISJP, LUSE, LUAE_NA, LUAE_FS, TSTRK1009, TSTRK1007, TSTRK1031, TSTRK1062, FK7210,
        FK7472, FK3226, FK7809, TS8071, FK7427, FK7426);

    private static final ImmutableList<IntygType> INCLUDED_IN_INTYG_FILTER = ImmutableList.of(SJUKPENNING, LUSE, LUAE_NA, LUAE_FS, FK3226,
        FK7809, FK7427, FK7426);


    private final String kodverksKod; //From https://riv-ta.atlassian.net/wiki/download/attachments/270532953/Kv%20intygstyp.xlsx
    private final String itIntygType; //The type name Intygtjansten is using and sends as metadata with all intyg
    private final String text;
    private final String shortText;
    private final boolean isSupportedIntyg;

    IntygType(String kodverksKod, String itIntygType, String shortText, String text, boolean isSupportedIntyg) {
        this.kodverksKod = kodverksKod;
        this.itIntygType = itIntygType;
        this.text = text;
        this.shortText = shortText;
        this.isSupportedIntyg = isSupportedIntyg;
    }

    public String getKodverksKod() {
        return kodverksKod;
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

    public static IntygType parseRivtaCode(String stringType) {
        for (IntygType intygType : values()) {
            if (intygType.kodverksKod != null && intygType.kodverksKod.equalsIgnoreCase(stringType)) {
                return intygType;
            }
        }
        return UNKNOWN;
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
