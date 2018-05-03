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
package se.inera.statistics.service.warehouse.sjukfallcalc;

import com.google.common.collect.Multimap;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;
import se.inera.statistics.service.warehouse.sjukfallcalc.perpatient.SjukfallPerPatientCalculator;

import java.util.List;

public class SjukfallPerPeriodCalculator {

    private final boolean extendSjukfall;
    private final List<Range> ranges;
    private final SjukfallPerPatientCalculator sjukfallPerPatientCalculator;
    private SjukfallCalculatorExtender sjukfallCalculatorExtender;

    /**
     * @param extendSjukfall
     *            true = försök att komplettera sjukfall från andra enheter än de man har tillgång till,
     *            false = titta bara på tillgängliga enheter, lämplig att använda t ex om man vet att man
     *            har tillgång till alla enheter
     */
    public SjukfallPerPeriodCalculator(boolean extendSjukfall, List<Range> ranges, List<Fact> aisle,
            Iterable<Fact> filteredAisle) {
        this.extendSjukfall = extendSjukfall;
        this.ranges = ranges;
        sjukfallPerPatientCalculator = new SjukfallPerPatientCalculator(ranges, filteredAisle);
        if (this.extendSjukfall) {
            sjukfallCalculatorExtender = new SjukfallCalculatorExtender(aisle);
        }
    }

    public Multimap<Long, SjukfallExtended> getSjukfallsForPeriod(int period) {
        Multimap<Long, SjukfallExtended> sjukfallsPerPatient = sjukfallPerPatientCalculator.getSjukfallsPerPatient(period);
        if (this.extendSjukfall) {
            this.sjukfallCalculatorExtender.extendSjukfallConnectedByIntygOnOtherEnhets(sjukfallsPerPatient);
        }
        return SjukfallCalculatorHelper.filterPersonifiedSjukfallsFromDate(ranges.get(period).getFrom(), sjukfallsPerPatient);
    }

}
