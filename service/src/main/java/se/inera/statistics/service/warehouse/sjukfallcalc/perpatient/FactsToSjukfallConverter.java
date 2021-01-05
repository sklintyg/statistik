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
package se.inera.statistics.service.warehouse.sjukfallcalc.perpatient;

import com.google.common.collect.ArrayListMultimap;
import java.util.Collection;
import java.util.List;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

public class FactsToSjukfallConverter {

    ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Iterable<Fact> facts) {
        return getSjukfallsPerPatient(facts, null);
    }

    ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Iterable<Fact> facts, Collection<Long> patientsFilter) {
        final ArrayListMultimap<Long, SjukfallExtended> sjukfallsPerPatient = ArrayListMultimap.create();
        if (facts == null) {
            return sjukfallsPerPatient;
        }
        for (Fact line : facts) {
            long key = line.getPatient();
            if (patientsFilter != null && !patientsFilter.contains(key)) {
                continue;
            }
            List<SjukfallExtended> sjukfallsForPatient = sjukfallsPerPatient.get(key);

            if (sjukfallsForPatient.isEmpty()) {
                SjukfallExtended sjukfall = new SjukfallExtended(line);
                sjukfallsPerPatient.put(key, sjukfall);
            } else {
                final SjukfallExtended sjukfall = sjukfallsForPatient.get(sjukfallsForPatient.size() - 1);
                SjukfallExtended nextSjukfall = sjukfall.join(line);
                if (nextSjukfall.isExtended()) {
                    sjukfallsForPatient.remove(sjukfallsForPatient.size() - 1);
                }
                sjukfallsPerPatient.put(key, nextSjukfall);
            }
        }
        return sjukfallsPerPatient;
    }

}
