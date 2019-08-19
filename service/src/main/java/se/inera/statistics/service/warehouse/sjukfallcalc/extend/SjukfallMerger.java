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
import java.util.List;
import java.util.Optional;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

public class SjukfallMerger {

    private ExtendedSjukfallCalculator extendedSjukfallCalculator;

    public SjukfallMerger(List<Fact> aisle) {
        this.extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
    }

    public void mergeAndUpdateSjukfall(long patient, Collection<SjukfallExtended> sjukfallsFromAvailableEnhetsForPatient,
        SjukfallExtended sjukfallFromAllVgForPatient) {
        List<SjukfallExtended> mergableSjukfalls = findAvailableSjukfallsTouchingSjukfall(sjukfallsFromAvailableEnhetsForPatient,
            sjukfallFromAllVgForPatient);
        SjukfallMergeHelper.mergeAllSjukfallInList(mergableSjukfalls)
            .ifPresent(sjukfallExtended -> updateMergedSjukfall(patient, sjukfallsFromAvailableEnhetsForPatient,
                sjukfallFromAllVgForPatient, mergableSjukfalls, sjukfallExtended));
    }

    private List<SjukfallExtended> findAvailableSjukfallsTouchingSjukfall(Collection<SjukfallExtended> availableSjukfalls,
        SjukfallExtended sjukfallToTouch) {
        return SjukfallMergeHelper.filterSjukfallInPeriod(sjukfallToTouch.getStart(), sjukfallToTouch.getEnd(), availableSjukfalls);
    }

    private void updateMergedSjukfall(long patient, Collection<SjukfallExtended> sjukfalls, SjukfallExtended sjukfall,
        List<SjukfallExtended> mergableSjukfalls, SjukfallExtended mergedSjukfall) {
        SjukfallExtended mergedSjukfallExtendedWithRealDays = mergedSjukfall.extendWithRealDaysWithinPeriod(sjukfall);
        mergedSjukfallExtendedWithRealDays = getSjukfallExtendedToOriginalStartDate(patient, sjukfalls,
            mergedSjukfallExtendedWithRealDays);
        for (SjukfallExtended mergableSjukfall : mergableSjukfalls) {
            sjukfalls.remove(mergableSjukfall);
        }
        sjukfalls.add(mergedSjukfallExtendedWithRealDays);
    }

    private SjukfallExtended getSjukfallExtendedToOriginalStartDate(long patient, Collection<SjukfallExtended> sjukfalls,
        SjukfallExtended mergedSjukfallExtendedWithRealDays) {
        SjukfallExtended returnSjukfall = mergedSjukfallExtendedWithRealDays;
        Optional<SjukfallExtended> firstSjukfall = SjukfallMergeHelper.getFirstSjukfall(sjukfalls);
        if (firstSjukfall.isPresent() && firstSjukfall.get().getStart() == mergedSjukfallExtendedWithRealDays.getStart()) {
            returnSjukfall = extendedSjukfallCalculator.getExtendedSjukfallStart(patient,
                mergedSjukfallExtendedWithRealDays);
        }
        return returnSjukfall;
    }

}
