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
package se.inera.statistics.service.warehouse;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import se.inera.statistics.hsa.model.HsaIdVardgivare;

import java.util.Date;

class SjukfallGroupCacheKey {

    private final LocalDate from;
    private final int periods;
    private final int periodSize;
    private final Aisle aisle;
    private final SjukfallFilter filter;
    private final boolean useOriginalSjukfallStart;
    private final String key;

    public SjukfallGroupCacheKey(LocalDate from, int periods, int periodSize, Aisle aisle, SjukfallFilter filter, boolean useOriginalSjukfallStart) {
        this.from = from;
        this.periods = periods;
        this.periodSize = periodSize;
        this.aisle = aisle;
        this.filter = filter;
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        this.key = getToTimeNullSafe(from) + "_" + periods + "_" + periodSize + "_" + getVardgivareIdNullSafe(aisle) + "_" + useOriginalSjukfallStart + "_" + getHashNullSafe(filter);
    }

    private String getHashNullSafe(SjukfallFilter filter) {
        if (filter == null) {
            return null;
        }
        return filter.getHash();
    }

    private HsaIdVardgivare getVardgivareIdNullSafe(Aisle aisle) {
        if (aisle == null) {
            return null;
        }
        return aisle.getVardgivareId();
    }

    private long getToTimeNullSafe(LocalDate from) {
        if (from == null) {
            return -1;
        }
        final Date date = from.toDate();
        if (date == null) {
            return -1;
        }
        return date.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SjukfallGroupCacheKey that = (SjukfallGroupCacheKey) o;

        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public LocalDate getFrom() {
        return from;
    }

    public int getPeriods() {
        return periods;
    }

    public int getPeriodSize() {
        return periodSize;
    }

    public Aisle getAisle() {
        return aisle;
    }

    public Predicate<Fact> getFilter() {
        return filter.getFilter();
    }

    public boolean isUseOriginalSjukfallStart() {
        return useOriginalSjukfallStart;
    }

    public String getKey() {
        return key;
    }
}
