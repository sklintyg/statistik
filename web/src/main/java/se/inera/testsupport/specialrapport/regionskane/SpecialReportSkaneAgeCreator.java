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
package se.inera.testsupport.specialrapport.regionskane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpecialReportSkaneAgeCreator {

    private static final Logger LOG = LoggerFactory.getLogger(SpecialReportSkaneAgeCreator.class);

    private final Iterator<Aisle> aisles;
    private final SjukfallUtil sjukfallUtil;

    private final Range period2016 = new Range(LocalDate.of(2016, 9, 1), LocalDate.of(2016, 12, 31));
    private final Range period2017 = new Range(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 12, 31));
    private final Range period2018 = new Range(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 12, 31));
    private final List<Range> periods = Arrays.asList(period2016, period2017, period2018);

    public SpecialReportSkaneAgeCreator(Iterator<Aisle> aisles, SjukfallUtil sjukfallUtil) {
        this.aisles = aisles;
        this.sjukfallUtil = sjukfallUtil;
    }

    public List<SpecialSkaneRow> getReport(boolean includeOngoingSjukfall, List<String> enhetsIn) {
        final List<String> enhetStringIds = SpecialReportSkaneCreator.getEnhetStrings(enhetsIn);
        final List<HsaIdEnhet> enhets = enhetStringIds.stream().map(HsaIdEnhet::new).collect(Collectors.toList());

        final List<SpecialSkaneAgeComputeRow> rows = new ArrayList<>();
        for (Iterator<Aisle> it = aisles; it.hasNext();) {
            Aisle aisle = it.next();
            for (Range period : periods) {
                final LocalDate fromDate = period.getFrom();
                final int startDay = WidelineConverter.toDay(fromDate);
                final Predicate<Fact> intygFilter = in -> enhetStringIds.contains(in.getEnhet().getId());
                final Predicate<Sjukfall> sjukfallPredicate = s -> includeOngoingSjukfall || s.getStart() >= startDay;
                final FilterPredicates filter = new FilterPredicates(intygFilter, sjukfallPredicate,
                        "SpecialSkane2019" + includeOngoingSjukfall, true);

                final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(period.getFrom(), 1,
                        period.getNumberOfMonths(), aisle, filter);

                for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                    final Collection<Sjukfall> sjukfalls = sjukfallGroup.getSjukfall();
                    final Range currentRange = sjukfallGroup.getRange();
                    final LocalDate startDateOfRange = currentRange.getFrom();
                    final int startDayOfRange = WidelineConverter.toDay(startDateOfRange);
                    for (Sjukfall sjukfall : sjukfalls) {
                        if (includeOngoingSjukfall || sjukfall.getStart() >= startDayOfRange) {
                            final long intygId = sjukfall.getFirstIntygId();
                            final Optional<Fact> fact = StreamSupport.stream(aisle.spliterator(), false)
                                    .filter(f -> f.getLakarintyg() == intygId).findAny();
                            if (!fact.isPresent()) {
                                LOG.warn("Fact from sjukfall not found..");
                                continue;
                            }
                            final int AgeDay = includeOngoingSjukfall ?
                                    Math.min(sjukfall.getEnd(), WidelineConverter.toDay(currentRange.getTo())) : sjukfall.getStart();
                            final int age = getAge(fact.get().getPatient(), WidelineConverter.toDate(AgeDay));
                            final Collection<HsaIdEnhet> enhetsInSjukfall = sjukfall.getEnhets();
                            final Optional<HsaIdEnhet> enhet = enhetsInSjukfall.stream().filter(s -> enhets.contains(s)).findAny();
                            if (enhet.isPresent()) {
                                final Kon kon = sjukfall.getKon();
                                final SpecialSkaneAgeComputeRow sosRow = new SpecialSkaneAgeComputeRow(age, enhet.get(), period, kon);
                                rows.add(sosRow);
                            }
                        }
                    }
                }
            }
        }

        final List<SpecialSkaneRow> results = new ArrayList<>();

        // Ålder nya sjukfall tot
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().getFrom().format(DateTimeFormatter.ISO_DATE),
                o -> "Genomsnittlig ålder för " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall " + o.getFormattedRange()));


        // Ålder nya sjukfall per kon
        results.addAll(resultadder(rows,
                r -> r.getEnhet() + r.getRange().getFrom().format(DateTimeFormatter.ISO_DATE) + r.getKon().name(),
                o -> "Genomsnittlig ålder för " + (includeOngoingSjukfall ? "pågående" : "nya") + " sjukfall "
                        + SpecialReportSkaneCreator.getGenderName(o.getKon()) + " " + o.getFormattedRange()));

        return results;
    }

    private static  int getAge(long pnr, LocalDate date) {
        final long yearPos = 100000000L;
        final long monthPos = 1000000L;
        final long dayPos = 10000L;
        final int year = (int) (pnr / yearPos);
        final int month = (int) ((pnr - year * yearPos) / monthPos);
        final int day = (int) ((pnr - year * yearPos - month * monthPos) / dayPos);
        final LocalDate birthdate = LocalDate.of(year, month, day);
        return (int) ChronoUnit.YEARS.between(birthdate, date);
    }

    private List<SpecialSkaneRow> resultadder(List<SpecialSkaneAgeComputeRow> rows, Function<SpecialSkaneAgeComputeRow,
            String> groupByFunction, Function<SpecialSkaneAgeComputeRow, String> rowName) {
        final List<SpecialSkaneRow> results = new ArrayList<>();
        final Map<String, List<SpecialSkaneAgeComputeRow>> rowsGroupedByFunction = rows.stream()
                .collect(Collectors.groupingBy(groupByFunction));
        for (Map.Entry<String, List<SpecialSkaneAgeComputeRow>> entry : rowsGroupedByFunction.entrySet()) {
            final SpecialSkaneAgeComputeRow firstRow = entry.getValue().get(0);
            results.add(new SpecialSkaneRow(rowName.apply(firstRow), firstRow.getEnhet().getId(),
                    entry.getValue().stream().mapToInt(SpecialSkaneAgeComputeRow::getAge).average().orElse(-2)));
        }
        return results;
    }

}
