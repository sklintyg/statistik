/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import se.inera.statistics.service.report.model.Range;

public class SjukfallGroup implements Serializable {

    private final Range range;
    private final Collection<Sjukfall> sjukfall;

    public SjukfallGroup(Range range, Collection<Sjukfall> sjukfall) {
        this.range = range;
        this.sjukfall = new ArrayList<>(sjukfall);
    }

    public Range getRange() {
        return range;
    }

    public Collection<Sjukfall> getSjukfall() {
        return sjukfall;
    }
}
