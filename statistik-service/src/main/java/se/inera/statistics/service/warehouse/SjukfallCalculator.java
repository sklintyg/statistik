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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Multimap;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.sjukfallcalc.SjukfallPerPeriodCalculator;

class SjukfallCalculator {

    private int period = 0;
    private final int maxPeriods;
    private SjukfallPerPeriodCalculator sjukfallPerPeriodCalculator;

    SjukfallCalculator(Aisle aisle, Predicate<Fact> filter, List<Range> ranges, boolean useOriginalSjukfallStart) {
        final ArrayList<Fact> facts = new ArrayList<>(aisle.getLines());
        boolean extendSjukfall = !SjukfallUtil.ALL_ENHETER.getIntygFilter().equals(filter);
        final Iterable<Fact> filteredAisle = StreamSupport.stream(aisle.spliterator(), true)
                .filter(filter).collect(Collectors.toList());
        ArrayList<Range> rangeList = new ArrayList<>(ranges);
        sjukfallPerPeriodCalculator = new SjukfallPerPeriodCalculator(extendSjukfall, useOriginalSjukfallStart, rangeList, facts,
                filteredAisle);
        maxPeriods = rangeList.size();
    }

    Collection<Sjukfall> getSjukfallsForNextPeriod() {
        Multimap<Long, SjukfallExtended> result = sjukfallPerPeriodCalculator.getSjukfallsForPeriod(period);
        period++;
        return result.values().stream().map(Sjukfall::create).collect(Collectors.toList());
    }

    boolean hasNextPeriod() {
        return period < maxPeriods;
    }

}
