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
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Collection;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;

public class IntygCommonGroup {
    private final Range range;
    private final Collection<IntygCommon> intyg;

    public IntygCommonGroup(Range range, Collection<IntygCommon> intyg) {
        this.range = range;
        this.intyg = new ArrayList<>(intyg);
    }

    public Range getRange() {
        return range;
    }

    public Collection<IntygCommon> getIntyg() {
        return intyg;
    }
}
