/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Iterator;

import se.inera.statistics.service.report.model.Range;


public class SjukfallIterator implements Iterator<SjukfallGroup> {

    private final LocalDate from;
    private int period = 0;
    private final int periods;
    private final int periodSize;
    private final SjukfallCalculator sjukfallCalculator;

    public SjukfallIterator(LocalDate from, int periods, int periodSize, Aisle aisle, Predicate<Fact> filter, boolean useOriginalSjukfallStart) {
        this.from = from;
        this.periods = periods;
        this.periodSize = periodSize;
        sjukfallCalculator = new SjukfallCalculator(aisle.getLines(), filter, useOriginalSjukfallStart);
    }

    @Override
    public boolean hasNext() {
        return period < periods;
    }

    @Override
    public SjukfallGroup next() {
        final LocalDate fromDate = from.plusMonths(period * periodSize);
        final LocalDate toDate = from.plusMonths((period + 1) * periodSize);
        Collection<Sjukfall> result = sjukfallCalculator.getSjukfalls(fromDate, toDate);

        Range range = new Range(fromDate, from.plusMonths(period * periodSize + periodSize - 1));
        SjukfallGroup sjukfallGroup = new SjukfallGroup(range, result);
        period++;
        return sjukfallGroup;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
