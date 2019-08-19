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
package se.inera.statistics.service.warehouse.sjukfallcalc.extend;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import se.inera.statistics.service.warehouse.SjukfallExtended;

final class SjukfallMergeHelper {

    private SjukfallMergeHelper() {
    }

    static Optional<SjukfallExtended> mergeAllSjukfallInList(List<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null) {
            return Optional.empty();
        }
        return sjukfalls.stream().reduce(SjukfallExtended::extendSjukfall);
    }

    static Optional<SjukfallExtended> getFirstSjukfall(Collection<SjukfallExtended> sjukfalls) {
        if (sjukfalls == null) {
            return Optional.empty();
        }
        return sjukfalls.stream().reduce((se1, se2) -> se1.getStart() < se2.getStart() ? se1 : se2);
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
