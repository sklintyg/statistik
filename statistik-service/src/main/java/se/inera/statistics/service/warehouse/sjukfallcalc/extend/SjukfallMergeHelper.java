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
package se.inera.statistics.service.warehouse.sjukfallcalc.extend;

import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class SjukfallMergeHelper {

    private SjukfallMergeHelper() {
    }

    static SjukfallExtended mergeAllSjukfallInList(List<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return null;
        }
        if (sjukfalls.size() == 1) {
            return sjukfalls.get(0);
        }
        sortByEndDate(sjukfalls);
        SjukfallExtended sjukfall = sjukfalls.get(0);
        for (int i = 1; i < sjukfalls.size(); i++) {
            final SjukfallExtended nextSjukfall = sjukfalls.get(i);
            sjukfall = sjukfall.extendSjukfall(nextSjukfall);
        }
        return sjukfall;
    }

    private static void sortByEndDate(List<SjukfallExtended> sjukfalls) {
        sjukfalls.sort((o1, o2) -> o1.getEnd() - o2.getEnd());
    }

    static SjukfallExtended getFirstSjukfall(Collection<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null) {
            return null;
        }
        SjukfallExtended currentFirstSjukfall = null;
        for (SjukfallExtended sjukfall : sjukfalls) {
            if (currentFirstSjukfall == null || sjukfall.getStart() < currentFirstSjukfall.getStart()) {
                currentFirstSjukfall = sjukfall;
            }
        }
        return currentFirstSjukfall;
    }

    static List<SjukfallExtended> filterSjukfallInPeriod(final int start, final int end, Collection<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null || sjukfalls.isEmpty()) {
            return Collections.emptyList();
        }
        return sjukfalls.stream()
                .filter(sjukfall -> sjukfall.getEnd() >= start && sjukfall.getStart() <= end)
                .collect(Collectors.toList());
    }

}
