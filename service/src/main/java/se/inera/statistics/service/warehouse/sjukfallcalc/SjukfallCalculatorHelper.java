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
package se.inera.statistics.service.warehouse.sjukfallcalc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import se.inera.statistics.service.warehouse.SjukfallExtended;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.time.LocalDate;
import java.util.Collection;

final class SjukfallCalculatorHelper {

    private SjukfallCalculatorHelper() {
    }

    static Multimap<Long, SjukfallExtended> filterPersonifiedSjukfallsFromDate(LocalDate from,
            Multimap<Long, SjukfallExtended> sjukfallsPerPatient) {
        final int firstday = WidelineConverter.toDay(from);
        Multimap<Long, SjukfallExtended> result = ArrayListMultimap.create();
        for (Long patient : sjukfallsPerPatient.keySet()) {
            final Collection<SjukfallExtended> sjukfalls = sjukfallsPerPatient.get(patient);
            for (SjukfallExtended sjukfall : sjukfalls) {
                if (sjukfall.getEnd() >= firstday) {
                    result.put(patient, sjukfall);
                }
            }
        }
        return result;
    }

}
