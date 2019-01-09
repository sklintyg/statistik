/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.testsupport.socialstyrelsenspecial;

import com.google.common.math.Quantiles;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SosMeCfs2ReportCreator {

    private final Iterator<Aisle> aisles;
    private final SjukfallUtil sjukfallUtil;
    private final Icd10 icd10;
    private final int cutoff;

    public SosMeCfs2ReportCreator(Iterator<Aisle> aisles, SjukfallUtil sjukfallUtil, int cutoff, Icd10 icd10) {
        this.aisles = aisles;
        this.sjukfallUtil = sjukfallUtil;
        this.cutoff = cutoff;
        this.icd10 = icd10;
    }

    public List<SosMeCfs2Row> getSosReport() {
        final int g933 = icd10.findFromIcd10Code("G933").toInt();
        final int g93 = icd10.findFromIcd10Code("G93").toInt();

        final ArrayList<SosMeCfs2ComputeRow> sosRows = new ArrayList<>();
        int totalNumberOfSjukfall = 0;
        int sjukfallWithMoreThanOneDx = 0;
        List<Integer> dxs = new ArrayList<>();

        for (Iterator<Aisle> it = aisles; it.hasNext();) {
            Aisle aisle = it.next();
            final Predicate<Sjukfall> sjukfallDxFilter = sjukfall -> g933 == sjukfall.getLastDx().getDiagnoskod();
            final LocalDate fromDate = LocalDate.of(2017, 1, 1);
            final LocalDate toDate = LocalDate.of(2017, 12, 31);
            final int fromDay = WidelineConverter.toDay(fromDate);
            final int toDay = WidelineConverter.toDay(toDate);
            final Predicate<Sjukfall> sjukfallDateFilter = sjukfall -> sjukfall.getEnd() >= fromDay && sjukfall.getEnd() <= toDay;
            Predicate<Sjukfall> sjukfallFilter = sjukfallDxFilter.and(sjukfallDateFilter);
            final FilterPredicates filter = new FilterPredicates(f -> true, sjukfallFilter, "sosspecial3", true);

            final Range range = new Range(fromDate, toDate);
            final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(range.getFrom(), 1,
                    range.getNumberOfMonths(), aisle, filter);

            final ArrayList<SosMeCfs2ComputeRow> sosRowsOnVg = new ArrayList<>();

            for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                final Collection<Sjukfall> sjukfalls = sjukfallGroup.getSjukfall();
                if (sjukfalls.size() >= cutoff) { // Apply cutoff for second part of report
                    for (Sjukfall sjukfall : sjukfalls) {
                        final Kon kon = sjukfall.getKon();
                        final int realDays = sjukfall.getRealDays();
                        final int age = sjukfall.getAlder();
                        final String ageGroup = AgeGroupSoc.getGroupForAge(age).orElse(AgeGroupSoc.GROUP1_0TO16).getGroupName();
                        final SosMeCfs2ComputeRow sosRow = new SosMeCfs2ComputeRow(kon, ageGroup, realDays);
                        sosRowsOnVg.add(sosRow);

                        totalNumberOfSjukfall++;
                        final List<Icd10RangeType> rangeTypes = Collections.singletonList(Icd10RangeType.KATEGORI);
                        final List<Integer> allIcd10OfTypes = sjukfall.getAllIcd10OfTypes(rangeTypes);
                        if (allIcd10OfTypes.size() > 1) {
                            sjukfallWithMoreThanOneDx++;
                            dxs.addAll(allIcd10OfTypes);
                        }
                    }
                }
            }
            //Apply cutoff for first part of report
            final Map<SosMeCfs2Tuple, List<SosMeCfs2ComputeRow>> vgRowsGroupedByGenderAndAgegroup = sosRowsOnVg.stream()
                    .collect(Collectors.groupingBy(p -> new SosMeCfs2Tuple(p.getKon(), p.getAgeGroup())));
            for (List<SosMeCfs2ComputeRow> sosMeCfs2ComputeRows : vgRowsGroupedByGenderAndAgegroup.values()) {
                if (sosMeCfs2ComputeRows.size() >= cutoff) {
                    sosRows.addAll(sosMeCfs2ComputeRows);
                }
            }
        }
        final Map<SosMeCfs2Tuple, List<SosMeCfs2ComputeRow>> groupedByGenderAndAgegroup = sosRows.stream()
                .collect(Collectors.groupingBy(p -> new SosMeCfs2Tuple(p.getKon(), p.getAgeGroup())));
        final List<SosMeCfs2Row> collect = groupedByGenderAndAgegroup.entrySet().stream().map(sos3TupleListEntry -> {
            final List<SosMeCfs2ComputeRow> rows = sos3TupleListEntry.getValue();
            final List<Integer> lengths = rows.stream().map(SosMeCfs2ComputeRow::getLength).collect(Collectors.toList());
            final double median = Quantiles.median().compute(lengths);
            final double q1 = Quantiles.quartiles().index(1).compute(lengths);
            final int three = 3;
            final double q3 = Quantiles.quartiles().index(three).compute(lengths);
            final Kon kon = sos3TupleListEntry.getKey().getKon();
            final String ageGroup = sos3TupleListEntry.getKey().getAgeGroup();
            return new SosMeCfs2Row(kon, ageGroup, rows.size(), median, q1, q3);
        }).collect(Collectors.toList());
        final double shareWithSeveralDxs = (double) sjukfallWithMoreThanOneDx / totalNumberOfSjukfall;
        collect.add(new SosMeCfs2Row(null, "Andel sjukfall med mer än 1 diagnos: " + shareWithSeveralDxs, 0, 0, 0, 0));
        final Map<Integer, Long> countedDxs = dxs.stream().collect(Collectors.groupingBy(t -> t, Collectors.counting()));
        final int maxNumberOfDxsToShow = 10;
        final List<Map.Entry<Integer, Long>> mostCombinedDxs = countedDxs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(r -> r.getKey() != g93).limit(maxNumberOfDxsToShow).collect(Collectors.toList());
        for (int i = 0; i < mostCombinedDxs.size(); i++) {
            final Map.Entry<Integer, Long> combinedDx = mostCombinedDxs.get(i);
            final String dxId = icd10.findIcd10FromNumericId(combinedDx.getKey()).getId();
            final String text = "Vanligaste annan diagnos #" + (i + 1) + " är " + dxId + " med " + combinedDx.getValue() + " förekomster";
            collect.add(new SosMeCfs2Row(null, text, 0, 0, 0, 0));
        }
        return collect;
    }

}
