/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SjukfallMerger {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallMerger.class);
    private ExtendedSjukfallCalculator extendedSjukfallCalculator;
    private final boolean useOriginalSjukfallStart;

    public SjukfallMerger(List<Fact> aisle, boolean useOriginalSjukfallStart) {
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        if (useOriginalSjukfallStart) {
            this.extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        }
    }

    public void mergeAndUpdateSjukfall(long patient, Collection<SjukfallExtended> sjukfallsFromAvailableEnhetsForPatient, SjukfallExtended sjukfallFromAllVgForPatient) {
        List<SjukfallExtended> mergableSjukfalls = findAvailableSjukfallsTouchingSjukfall(sjukfallsFromAvailableEnhetsForPatient, sjukfallFromAllVgForPatient);
        SjukfallMergeHelper.mergeAllSjukfallInList(mergableSjukfalls)
                .ifPresent(sjukfallExtended -> updateMergedSjukfall(patient, sjukfallsFromAvailableEnhetsForPatient, sjukfallFromAllVgForPatient, mergableSjukfalls, sjukfallExtended));
    }

    private List<SjukfallExtended> findAvailableSjukfallsTouchingSjukfall(Collection<SjukfallExtended> availableSjukfalls, SjukfallExtended sjukfallToTouch) {
        return SjukfallMergeHelper.filterSjukfallInPeriod(sjukfallToTouch.getStart(), sjukfallToTouch.getEnd(), availableSjukfalls);
    }

    private void updateMergedSjukfall(long patient, Collection<SjukfallExtended> sjukfalls, SjukfallExtended sjukfall, List<SjukfallExtended> mergableSjukfalls, SjukfallExtended mergedSjukfall) {
        SjukfallExtended mergedSjukfallExtendedWithRealDays = mergedSjukfall.extendWithRealDaysWithinPeriod(sjukfall);
        if (useOriginalSjukfallStart) {
            mergedSjukfallExtendedWithRealDays = getSjukfallExtendedToOriginalStartDate(patient, sjukfalls, mergedSjukfallExtendedWithRealDays);
        }
        for (SjukfallExtended mergableSjukfall : mergableSjukfalls) {
            sjukfalls.remove(mergableSjukfall);
        }
        sjukfalls.add(mergedSjukfallExtendedWithRealDays);
    }

    private SjukfallExtended getSjukfallExtendedToOriginalStartDate(long patient, Collection<SjukfallExtended> sjukfalls, SjukfallExtended mergedSjukfallExtendedWithRealDays) {
        Optional<SjukfallExtended> firstSjukfall = SjukfallMergeHelper.getFirstSjukfall(sjukfalls);
        if (firstSjukfall.isPresent() && firstSjukfall.get().getStart() == mergedSjukfallExtendedWithRealDays.getStart()) {
            mergedSjukfallExtendedWithRealDays = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, mergedSjukfallExtendedWithRealDays);
        }
        return mergedSjukfallExtendedWithRealDays;
    }

}
