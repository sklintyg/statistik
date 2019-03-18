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
package se.inera.testsupport.specialrapport;

import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;

public class SjukfallLengthSpecialReportCreator {

    public static final int MAX_SIZE = 10;
    public static final int PERCENTAGE = 100;
    private final Iterator<Aisle> aisles;
    private final SjukfallUtil sjukfallUtil;
    private final Icd10 icd10;

    public SjukfallLengthSpecialReportCreator(Iterator<Aisle> aisles, SjukfallUtil sjukfallUtil, Icd10 icd10) {
        this.aisles = aisles;
        this.sjukfallUtil = sjukfallUtil;
        this.icd10 = icd10;
    }

    public List<SjukfallLengthSpecialRow> getReport(int year) {
        final List<SjukfallLengthSpecialComputeRow> rows = new ArrayList<>();

        for (Iterator<Aisle> it = aisles; it.hasNext();) {
            Aisle aisle = it.next();
            final LocalDate fromDate = LocalDate.of(year, 1, 1);
            final LocalDate toDate = LocalDate.of(year, 12, 31);
            final int endDay = WidelineConverter.toDay(toDate);
            final FilterPredicates filter = new FilterPredicates(f -> true, s -> true, "SpecialLength", true);

            final Range range = new Range(fromDate, toDate);

            //Must include one extra month to get any sjukfall that is continued after the
            // period but has no active intyg when the period ends.
            final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(range.getFrom(), 1,
                    range.getNumberOfMonths() + 1, aisle, filter);

            for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                final Collection<Sjukfall> sjukfalls = sjukfallGroup.getSjukfall();
                    for (Sjukfall sjukfall : sjukfalls) {
                        if (sjukfall.getEnd() <= endDay) {
                            final int realDays = sjukfall.getRealDays();
                            final int kat = sjukfall.getDiagnoskategori();
                            final SjukfallsLangdGroupSpecial lengthGroup = getByLength(realDays);

                            final SjukfallLengthSpecialComputeRow sosRow = new SjukfallLengthSpecialComputeRow(kat, lengthGroup);
                            rows.add(sosRow);
                        }
                    }
            }
        }

        final Map<SjukfallsLangdGroupSpecial, List<SjukfallLengthSpecialComputeRow>> rowsGroupedByLength = getrowsGroupedByLength(rows);
        final List<SjukfallLengthSpecialRow> results = rowsGroupedByLength.entrySet().stream()
                .map(p -> new SjukfallLengthSpecialRow(p.getKey().getGroupName(), p.getValue().size()))
                .collect(Collectors.toList());

        final List<SjukfallsLangdGroupSpecial> groups91To180 = Collections.singletonList(SjukfallsLangdGroupSpecial.GROUP6_91TO180);
        results.addAll(getSjukfallLengthSpecialRows(groups91To180, "91-180 dagar", rowsGroupedByLength));
        final List<SjukfallsLangdGroupSpecial> groupsMoreThan180 = Arrays.asList(
                SjukfallsLangdGroupSpecial.GROUP7_181TO365, SjukfallsLangdGroupSpecial.GROUP8_366PLUS);
        results.addAll(getSjukfallLengthSpecialRows(groupsMoreThan180, "över 180 dagar", rowsGroupedByLength));
        return results;
    }

    private Map<SjukfallsLangdGroupSpecial, List<SjukfallLengthSpecialComputeRow>> getrowsGroupedByLength(
            List<SjukfallLengthSpecialComputeRow> rows) {
        final Map<SjukfallsLangdGroupSpecial, List<SjukfallLengthSpecialComputeRow>> rowsGroupedByLength = rows.stream()
                .collect(Collectors.groupingBy(SjukfallLengthSpecialComputeRow::getLength));
        for (SjukfallsLangdGroupSpecial slgs : SjukfallsLangdGroupSpecial.values()) {
            rowsGroupedByLength.putIfAbsent(slgs, Collections.emptyList());
        }
        return rowsGroupedByLength;
    }

    private List<SjukfallLengthSpecialRow> getSjukfallLengthSpecialRows(
            Collection<SjukfallsLangdGroupSpecial> groups, String groupName,
            Map<SjukfallsLangdGroupSpecial, List<SjukfallLengthSpecialComputeRow>> rowsGroupedByLength) {
        long totalNumberOfSjukfallInGroups = rowsGroupedByLength.entrySet().stream()
                .filter(sjukfallsLangdGroupSpecialListEntry -> groups.contains(sjukfallsLangdGroupSpecialListEntry.getKey()))
                .mapToLong(sjukfallsLangdGroupSpecialListEntry -> sjukfallsLangdGroupSpecialListEntry.getValue().size())
                .sum();
        List<SjukfallLengthSpecialRow> rows = new ArrayList<>();
        final List<String> topList = rowsGroupedByLength.entrySet().stream()
                .filter(sjukfallsLangdGroupSpecialListEntry -> groups.contains(sjukfallsLangdGroupSpecialListEntry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap((Function<List<SjukfallLengthSpecialComputeRow>, Stream<Integer>>) calcRows -> calcRows.stream()
                        .map(SjukfallLengthSpecialComputeRow::getKat))
                .collect(Collectors.groupingBy(Function.identity(), counting())).entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(MAX_SIZE)
                .map(amount -> icd10.findIcd10FromNumericId(amount.getKey()).getVisibleId() + " med "
                        + amount.getValue() + " förekomster" + " ("
                        + (PERCENTAGE * amount.getValue() / (double) totalNumberOfSjukfallInGroups)
                        + "% av " + totalNumberOfSjukfallInGroups + ")")
                .collect(Collectors.toList());
        for (int i = 0; i < topList.size(); i++) {
            final String s = topList.get(i);
            rows.add(new SjukfallLengthSpecialRow("Vanligaste diagnos i sjukfall " + groupName + " #" + (i + 1) + " är " + s, 0));
        }
        return rows;
    }

    private SjukfallsLangdGroupSpecial getByLength(int realDays) {
        try {
            return SjukfallsLangdGroupSpecial.getByLength(realDays);
        } catch (Exception e) {
            return null;
        }
    }

}
