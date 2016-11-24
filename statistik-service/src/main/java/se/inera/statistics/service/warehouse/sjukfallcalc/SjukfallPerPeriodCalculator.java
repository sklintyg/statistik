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
package se.inera.statistics.service.warehouse.sjukfallcalc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SjukfallPerPeriodCalculator {

    private final boolean extendSjukfall; //true = försök att komplettera sjukfall från andra enheter än de man har tillgång till, false = titta bara på tillgängliga enheter, lämplig att använda t ex om man vet att man har tillgång till alla enheter
    private final boolean useOriginalSjukfallStart;
    private final List<Range> ranges;
    private final List<Fact> aisle;
    private SjukfallCalculatorExtendHelper sjukfallCalculatorExtendHelper;
    private List<ArrayListMultimap<Long, Fact>> factsPerPatientAndPeriod;

    /**
     * @param useOriginalSjukfallStart true = använd faktiskt startdatum, inte första datum på första intyget som är tillgängligt för anroparen
     */
    public SjukfallPerPeriodCalculator(boolean extendSjukfall, boolean useOriginalSjukfallStart, List<Range> ranges, List<Fact> aisle, Iterable<Fact> filteredAisle) {
        this.extendSjukfall = extendSjukfall;
        this.useOriginalSjukfallStart = useOriginalSjukfallStart;
        this.ranges = ranges;
        this.aisle = aisle;
        factsPerPatientAndPeriod = FactsPerPatientAndPeriodGrouper.group(filteredAisle, this.ranges);
    }

    public Multimap<Long, SjukfallExtended> getSjukfallsForPeriod(int period) {
        Multimap<Long, SjukfallExtended> sjukfallsPerPatient = getSjukfallsPerPatient(period);
        if (extendSjukfall) {
            if (sjukfallCalculatorExtendHelper == null) {
                sjukfallCalculatorExtendHelper = new SjukfallCalculatorExtendHelper(this.useOriginalSjukfallStart, this.aisle);
            }
            sjukfallCalculatorExtendHelper.extendSjukfallConnectedByIntygOnOtherEnhets(sjukfallsPerPatient);
        }
        return filterPersonifiedSjukfallsFromDate(ranges.get(period).getFrom(), sjukfallsPerPatient);
    }

    private Multimap<Long, SjukfallExtended> getSjukfallsPerPatient(int period) {
        final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = getSjukfallPerPatientInPeriod(period);
        if (useOriginalSjukfallStart) {
            Collection<Long> allPatiensWithPossibleEarierStart = getAllPatientWithPossibleEarlierStart(ranges.get(period), sjukfalls);
            final ArrayListMultimap<Long, SjukfallExtended> allSjukfallsForEarlyPatient = getAllSjukfallsForPatients(period, allPatiensWithPossibleEarierStart);
            for (Map.Entry<Long, Collection<SjukfallExtended>> entry : allSjukfallsForEarlyPatient.asMap().entrySet()) {
                sjukfalls.replaceValues(entry.getKey(), entry.getValue());

            }
        }
        return sjukfalls;
    }

    private ArrayListMultimap<Long, SjukfallExtended> getAllSjukfallsForPatients(int period, Collection<Long> patientToCalculate) {
        final ArrayListMultimap<Long, Fact> factsPerPatient = ArrayListMultimap.create();
        for (int i = 0; i <= (period + 1); i++) {
            factsPerPatient.putAll(factsPerPatientAndPeriod.get(i));
        }
        final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create();
        final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
        for (Long patientId : factsPerPatient.keySet()) {
            final boolean shouldCalculateForThisPatient = patientToCalculate.contains(patientId);
            if (shouldCalculateForThisPatient && (period == 0 || !factsPerPatientInPeriod.get(patientId).isEmpty())) {
                sjukfalls.putAll(getSjukfallsPerPatient(factsPerPatient.get(patientId)));
            }
        }
        return sjukfalls;
    }

    private Collection<Long> getAllPatientWithPossibleEarlierStart(Range range, ArrayListMultimap<Long, SjukfallExtended> sjukfalls) {
        final int latestStartDate = WidelineConverter.toDay(range.getFrom()) + SjukfallExtended.MAX_GAP;
        return sjukfalls.asMap().entrySet().stream()
                .filter(longCollectionEntry -> longCollectionEntry.getValue().stream()
                        .anyMatch(sjukfallExtended -> sjukfallExtended.getStart() <= latestStartDate))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallPerPatientInPeriod(int period) {
        final ArrayListMultimap<Long, SjukfallExtended> sjukfalls = ArrayListMultimap.create();
        final ArrayListMultimap<Long, Fact> factsPerPatientInPeriod = factsPerPatientAndPeriod.get(period + 1);
        for (Long patientId : factsPerPatientInPeriod.keySet()) {
            sjukfalls.putAll(getSjukfallsPerPatient(factsPerPatientInPeriod.get(patientId)));
        }
        return sjukfalls;
    }

    private Multimap<Long, SjukfallExtended> filterPersonifiedSjukfallsFromDate(LocalDate from, Multimap<Long, SjukfallExtended> sjukfallsPerPatient) {
        final int firstday = WidelineConverter.toDay(from);
        Multimap<Long, SjukfallExtended> result = ArrayListMultimap.create();
        for (Long patient : sjukfallsPerPatient.keySet()) {
            final Collection<SjukfallExtended> sjukfalls = sjukfallsPerPatient.get(patient);
            for (SjukfallExtended sjukfall : sjukfalls) {
                if (sjukfall.getEnd() >= firstday) {
                    result.put(patient, sjukfall);
                }
            }
        }
        return result;
    }

    private ArrayListMultimap<Long, SjukfallExtended> getSjukfallsPerPatient(Iterable<Fact> facts) {
        return SjukfallCalculatorHelper.getSjukfallsPerPatient(facts, null);
    }

}
