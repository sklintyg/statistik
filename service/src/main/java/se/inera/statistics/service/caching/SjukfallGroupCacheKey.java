/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.caching;

import java.time.LocalDate;
import java.util.Date;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;

public class SjukfallGroupCacheKey {

    private final LocalDate from;
    private final int periods;
    private final int periodSize;
    private final Aisle aisle;
    private final FilterPredicates filter;
    private final String key;

    public SjukfallGroupCacheKey(LocalDate from, int periods, int periodSize, Aisle aisle, FilterPredicates filter) {
        this.from = from;
        this.periods = periods;
        this.periodSize = periodSize;
        this.aisle = aisle;
        this.filter = filter;
        this.key = getToTimeNullSafe(from) + "_" + periods + "_" + periodSize + "_" + getVardgivareIdNullSafe(aisle) + "_"
            + getHashNullSafe(filter);
    }

    private String getHashNullSafe(FilterPredicates filter) {
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
        final Date date = new Date(from.toEpochDay());
        return date.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SjukfallGroupCacheKey)) {
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

    public FilterPredicates getFilter() {
        return filter;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
