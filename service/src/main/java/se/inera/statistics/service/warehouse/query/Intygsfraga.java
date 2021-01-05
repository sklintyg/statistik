/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.query;

import java.util.Arrays;
import java.util.Optional;

public enum Intygsfraga {
    AKTBEGR("Aktivitetsbegränsning", 17),
    ARB_ATG("Arbetslivsinriktade åtgärder", 40, 44),
    ARBFORL("Arbetstidsförläggning", 33),
    ARBRES("Arbetsresor", 34),
    BEDOMNING("Bedömning av patientens nedsättning av arbetsförmåga", 32, 37),
    DIAGNOS("Diagnos(er)", 6),
    FUNKNED("Funktionsnedsättning", 35),
    BASERAT("Intyget baseras på", 1),
    KONTAKT_FK("Kontakt med Försäkringskassan", 26),
    NUVARANDE_ARB("Nuvarande yrke och arbetsuppgifter", 29),
    PLAN_BEHAND("Planerade medicinska behandlingar/åtgärder", 20),
    PROGNOS("Prognos för arbetsförmåga", 39),
    PAGANDE_BEHAND("Pågående medicinska behandlingar/åtgärder", 19),
    SMITT("Smittbärarpenning", 27),
    SYSSELSATTNING("Sysselsättning", 28),
    OVRIGA("Övriga upplysningar", 25);

    private final String text;
    @SuppressWarnings("ImmutableEnumChecker") //frageIds is not accessible from outside this class and mutability can hence be ignored
    private final int[] frageIds;

    Intygsfraga(String text, int... frageIds) {
        this.text = text;
        this.frageIds = frageIds;
        Arrays.sort(this.frageIds);
    }

    public String getText() {
        return text;
    }

    public static Optional<Intygsfraga> getByFrageid(int id) {
        final Intygsfraga[] values = values();
        for (Intygsfraga value : values) {
            final int i = Arrays.binarySearch(value.frageIds, id);
            if (i >= 0) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
