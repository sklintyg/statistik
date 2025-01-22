/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import java.util.Collection;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Range;

public class IntygCommonFilter {

    private final Range range;
    private final Collection<HsaIdEnhet> enheter;
    private final Collection<String> diagnoser;
    private final Collection<String> aldersgrupp;
    private final Collection<String> intygstyper;

    public IntygCommonFilter(Range range, Collection<HsaIdEnhet> enheter, Collection<String> diagnoser,
        Collection<String> aldersgrupp, Collection<String> intygstyper) {
        this.range = range;
        this.enheter = enheter;
        this.diagnoser = diagnoser;
        this.aldersgrupp = aldersgrupp;
        this.intygstyper = intygstyper;
    }

    public Range getRange() {
        return range;
    }

    public Collection<HsaIdEnhet> getEnheter() {
        return enheter;
    }

    public Collection<String> getDiagnoser() {
        return diagnoser;
    }

    public Collection<String> getAldersgrupp() {
        return aldersgrupp;
    }

    public Collection<String> getIntygstyper() {
        return intygstyper;
    }
}
