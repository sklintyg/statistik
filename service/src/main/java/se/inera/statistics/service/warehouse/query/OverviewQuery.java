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
package se.inera.statistics.service.warehouse.query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

@Component
public class OverviewQuery {

    public static final int PERCENT = 100;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    public VerksamhetOverviewResponse getOverview(Aisle aisle, FilterPredicates filter, Range currentPeriod, Range previousPeriod) {
        SjukfallGroup currentSjukfall = getSjukfallGroup(aisle, filter, currentPeriod);
        SjukfallGroup previousSjukfall = getSjukfallGroup(aisle, filter, previousPeriod);

        OverviewKonsfordelning previousKonsfordelning = getOverviewKonsfordelning(previousSjukfall.getRange(),
                previousSjukfall.getSjukfall());
        OverviewKonsfordelning currentKonsfordelning = getOverviewKonsfordelning(currentSjukfall.getRange(), currentSjukfall.getSjukfall());

        int currentLongSjukfall = SjukskrivningslangdQuery.getLong(currentSjukfall.getSjukfall());
        int previousLongSjukfall = SjukskrivningslangdQuery.getLong(previousSjukfall.getSjukfall());

        List<OverviewChartRowExtended> aldersgrupper = AldersgruppQuery.getOverviewAldersgrupper(currentSjukfall.getSjukfall(),
                previousSjukfall.getSjukfall(), Integer.MAX_VALUE);
        List<OverviewChartRowExtended> diagnosgrupper = query.getOverviewDiagnosgrupper(currentSjukfall.getSjukfall(),
                previousSjukfall.getSjukfall(), Integer.MAX_VALUE);
        List<OverviewChartRowExtended> sjukskrivningsgrad = SjukskrivningsgradQuery
                .getOverviewSjukskrivningsgrad(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall());
        List<OverviewChartRow> sjukskrivningslangd = SjukskrivningslangdQuery
                .getOverviewSjukskrivningslangd(currentSjukfall.getSjukfall(), Integer.MAX_VALUE);

        return new VerksamhetOverviewResponse(AvailableFilters.getForSjukfall(), currentSjukfall.getSjukfall().size(),
                currentKonsfordelning, previousKonsfordelning, diagnosgrupper, aldersgrupper, sjukskrivningsgrad,
                sjukskrivningslangd, currentLongSjukfall, percentChange(currentLongSjukfall, previousLongSjukfall));
    }

    private SjukfallGroup getSjukfallGroup(Aisle aisle, FilterPredicates filter, Range range) {
        final LocalDate from = range.getFrom();
        final int periodLength = range.getNumberOfMonths();
        return sjukfallUtil.sjukfallGrupper(from, 1, periodLength, aisle, filter).iterator().next();
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    OverviewKonsfordelning getOverviewKonsfordelning(Range range, Collection<Sjukfall> sjukfalls) {
        int male = countMale(sjukfalls);
        int female = sjukfalls.size() - male;

        return new OverviewKonsfordelning(male, female, range);
    }

    public static int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == Kon.MALE) {
                count++;
            }
        }
        return count;
    }

}
