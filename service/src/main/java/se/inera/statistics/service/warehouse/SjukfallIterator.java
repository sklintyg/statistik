/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import se.inera.statistics.service.report.model.Range;

public class SjukfallIterator implements Iterator<SjukfallGroup> {

    private final LocalDate from;
    private final FilterPredicates sjukfallFilter;
    private int period = 0;
    private final int periodSize;
    private final SjukfallCalculator sjukfallCalculator;

    public SjukfallIterator(LocalDate from, int periods, int periodSize, Aisle aisle, FilterPredicates sjukfallFilter) {
        this.from = from;
        this.periodSize = periodSize;
        this.sjukfallFilter = sjukfallFilter;
        List<Range> ranges = getRanges(from, periods, periodSize);
        sjukfallCalculator = getSjukfallCalculator(aisle, sjukfallFilter.getIntygFilter(), ranges);
    }

    SjukfallCalculator getSjukfallCalculator(Aisle aisle, Predicate<Fact> filter, List<Range> ranges) {
        return new SjukfallCalculator(aisle, filter, ranges);
    }

    @SuppressFBWarnings(value = "ICAST_INTEGER_MULTIPLY_CAST_TO_LONG",
        justification = "We know what we're doing. No no risk for integer overflow.")
    public static List<Range> getRanges(LocalDate from, int periods, int periodSize) {
        final ArrayList<Range> ranges = new ArrayList<>();
        for (int i = 0; i < periods; i++) {
            final LocalDate fromDate = from.plusMonths(i * periodSize).withDayOfMonth(1);
            final LocalDate toDate = from.plusMonths((i + 1) * periodSize).withDayOfMonth(1).minusDays(1);
            ranges.add(new Range(fromDate, toDate));
        }
        return ranges;
    }

    @Override
    public boolean hasNext() {
        return sjukfallCalculator.hasNextPeriod();
    }

    @Override
    @SuppressFBWarnings(value = "ICAST_INTEGER_MULTIPLY_CAST_TO_LONG",
        justification = "We know what we're doing. No no risk for integer overflow.")
    public SjukfallGroup next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        List<Sjukfall> result = sjukfallCalculator.getSjukfallsForNextPeriod().stream()
            .filter(t -> sjukfallFilter.getSjukfallFilter().test(t)).collect(Collectors.toList());
        final LocalDate fromDate = from.plusMonths(period * periodSize);
        Range range = new Range(fromDate,
            from.plusMonths(period * periodSize + periodSize - 1).plusMonths(1).withDayOfMonth(1).minusDays(1));
        SjukfallGroup sjukfallGroup = new SjukfallGroup(range, result);
        period++;
        return sjukfallGroup;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
