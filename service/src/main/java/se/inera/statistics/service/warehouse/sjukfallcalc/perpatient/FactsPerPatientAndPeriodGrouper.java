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
package se.inera.statistics.service.warehouse.sjukfallcalc.perpatient;

import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.List;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.WidelineConverter;

final class FactsPerPatientAndPeriodGrouper {

    private FactsPerPatientAndPeriodGrouper() {
    }

    static List<ArrayListMultimap<Long, Fact>> group(Iterable<Fact> facts, List<Range> ranges) {
        final List<Integer> rangeEnds = toRangeEnds(ranges);
        return getFactsPerPatientAndPeriod(facts, rangeEnds);
    }

    private static List<ArrayListMultimap<Long, Fact>> initFactsPerPatientAndPeriod(List<Integer> rangeEnds) {
        List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = new ArrayList<>(rangeEnds.size());
        for (int i = 0; i < rangeEnds.size(); i++) {
            factsPerPatientAndPeriod.add(ArrayListMultimap.create());
        }
        return factsPerPatientAndPeriod;
    }

    private static List<Integer> toRangeEnds(List<Range> ranges) {
        final List<Integer> rangeEnds = new ArrayList<>(ranges.size() + 1);
        rangeEnds.add(WidelineConverter.toDay(ranges.get(0).getFrom().minusDays(1)));
        for (Range range : ranges) {
            rangeEnds.add(WidelineConverter.toDay(range.getTo()));
        }
        rangeEnds.add(Integer.MAX_VALUE);
        return rangeEnds;
    }

    private static List<ArrayListMultimap<Long, Fact>> getFactsPerPatientAndPeriod(Iterable<Fact> facts, List<Integer> rangeEnds) {
        List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod = initFactsPerPatientAndPeriod(rangeEnds);
        for (Fact fact : facts) {
            final List<Integer> rangeIndexes = getRangeIndexes(fact.getStartdatum(), fact.getSlutdatum(), rangeEnds);
            for (Integer rangeIndex : rangeIndexes) {
                if (rangeIndex >= 0) {
                    factsPerPatientAndPeriod.get(rangeIndex).put(fact.getPatient(), fact);
                }
            }
        }
        return factsPerPatientAndPeriod;
    }

    private static List<Integer> getRangeIndexes(int startdatum, int slutdatum, List<Integer> rangeEnds) {
        final int startIndex = getRangeIndex(startdatum, rangeEnds);
        final int slutIndex = getRangeIndex(slutdatum, rangeEnds);
        final ArrayList<Integer> indexes = new ArrayList<>();
        if (startIndex >= 0 && slutIndex >= 0) {
            for (int i = startIndex; i <= slutIndex; i++) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private static int getRangeIndex(int date, List<Integer> rangeEnds) {
        final int rangesSize = rangeEnds.size();
        for (int i = 0; i < rangesSize; i++) {
            final Integer rangeEnd = rangeEnds.get(i);
            if (date <= rangeEnd) {
                return i;
            }
        }
        return -1;
    }

}
