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
package se.inera.testsupport.fkrapport;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallIterator;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

/**
 * Creates a flattened multidimensional report of sjukfalls-lengths per diagnos, sex and county.
 */
public class FkReportCreator {

    private static final Logger LOG = LoggerFactory.getLogger(FkReportCreator.class);

    private static final String ALL_DIAGNOSES_ENTRY = "Alla";
    private static final String MATCH_ANYTHING_REGEXP = "(.*)";
    private Lan lan = new Lan();

    private final Iterator<Aisle> aisles;
    private Icd10 icd10;
    private List<String> diagnoseCategories;
    private Clock clock;

    public FkReportCreator(Iterator<Aisle> aisles, Icd10 icd10, List<String> diagnoseCategories, Clock clock) {
        this.aisles = aisles;
        this.icd10 = icd10;
        this.diagnoseCategories = diagnoseCategories;
        this.clock = clock;
    }

    public List<FkReportDataRow> getReportData() {
        List<FkFactRow> facts = getEffectiveFactRows();
        List<FkReportDataRow> results = createResultRowsForDiagnoses();

        // return resultset with the facts distributed over the rows
        return distributeFactRows(facts, results);
    }

    List<FkReportDataRow> distributeFactRows(List<FkFactRow> facts, List<FkReportDataRow> results) {
        facts.forEach(fkFactRow -> results.forEach(fkResultRow -> fkResultRow.ingest(fkFactRow)));
        return results;
    }

    List<FkReportDataRow> createResultRowsForDiagnoses() {
        // Create an expanded list of all possible diagnose entries to distribute statistics for.
        List<DiagnoseEntry> diagnoseEntries = buildDiagnoseEntries();

        List<FkReportDataRow> rows = new ArrayList<>();

        diagnoseEntries.forEach(diagnoseEntry -> lan.forEach(lansId -> {
            rows.add(new FkReportDataRow(diagnoseEntry.getDiagnoseValue(), diagnoseEntry.getDiagnoseMatcher(), Kon.FEMALE, lansId));
            rows.add(new FkReportDataRow(diagnoseEntry.getDiagnoseValue(), diagnoseEntry.getDiagnoseMatcher(), Kon.MALE, lansId));
            rows.add(new FkReportDataRow(diagnoseEntry.getDiagnoseValue(), diagnoseEntry.getDiagnoseMatcher(), null, lansId));
        }));

        return rows;
    }

    /**
     * Create the list of DiagnoseEntries that the resultset will be based on.
     *
     * @return The expanded list of all diagnoses to match
     */
    private List<DiagnoseEntry> buildDiagnoseEntries() {
        List<DiagnoseEntry> result = new ArrayList<>();

        // First, add a static "Alla" diagnose entry that matches all codes.
        result.add(new DiagnoseEntry(ALL_DIAGNOSES_ENTRY, MATCH_ANYTHING_REGEXP));

        diagnoseCategories.forEach(diagnoseCategory -> {

            // Add exact match for each category, e.g "F32"
            result.add(new DiagnoseEntry(diagnoseCategory, diagnoseCategory));

            // Add wildcard matcher for all children, e.g "F32XXXXX"
            result.add(new DiagnoseEntry(diagnoseCategory + "+", diagnoseCategory + MATCH_ANYTHING_REGEXP));

            // ..and finally add exact matchers for all existing subdiagnoses, e.g F320 etc.
            result.addAll(getExactMatchesForSubdiagnoses(diagnoseCategory));
        });

        return result;
    }

    /**
     * Fetches alla sub items for a given diagnose category, and adds (exact) matches for all those.
     * Throws IllegalArgumentException if the diagnoseCategory is missing from the Icd10 repository
     *
     * @param diagnoseCategory The category (e.g "F32") to create matches for
     */
    private List<DiagnoseEntry> getExactMatchesForSubdiagnoses(String diagnoseCategory) {
        final Icd10.Kategori kategori = icd10.getKategori(diagnoseCategory);
        if (kategori == null) {
            throw new IllegalArgumentException("Diagnose category " + diagnoseCategory + " not found in ICD10 registry");
        }
        List<DiagnoseEntry> result = new ArrayList<>();
        kategori.getSubItems().forEach(id -> result.add(new DiagnoseEntry(id.getId(), id.getId())));
        return result;
    }

    private List<FkFactRow> getEffectiveFactRows() {
        final LocalDate from = getFirstDateOfLastYear(); // 2015-01-01
        final Range range = new Range(from, LocalDate.now(clock));

        final int fromIntDay = WidelineConverter.toDay(from); // dagnr relativt 2000-01-01
        final int toIntDay = WidelineConverter.toDay(getLastDateOfLastYear()); // dagnr för 2015-12-31 relativt
        // 2000-01-01

        LOG.info("About to iterate aisles");
        // Loopa igenom ALLA facts per VG, dvs ingen sammanslagning sker (viktigt av juridiska skäl)
        Iterable<Aisle> iterableAisles = () -> aisles;
        return StreamSupport.stream(iterableAisles.spliterator(), true)
            .flatMap(aisle -> getFkFactRowsPerVg(range, fromIntDay, toIntDay, SjukfallUtil.ALL_ENHETER, aisle))
            .collect(Collectors.toList());
    }

    private Stream<FkFactRow> getFkFactRowsPerVg(Range range, int fromIntDay, int toIntDay, FilterPredicates sjukfallFilter,
        Aisle aisle) {
        LOG.info("About to get sjukfall for vg  " + aisle.getVardgivareId());
        // Hämta ut dom som hör till detta år
        final ArrayList<SjukfallGroup> sjukfallGroups = Lists
            .newArrayList(new SjukfallIterator(range.getFrom(), 1, range.getNumberOfMonths(), aisle, sjukfallFilter));
        return sjukfallGroups.stream()
            .flatMap(sjukfallGroup -> sjukfallGroup.getSjukfall().stream())
            .collect(Collectors.toMap(Sjukfall::getFirstIntygId, p -> p, (p, q) -> p)).values().stream() // distinct by property
            .filter(sjukfall -> inPeriod(sjukfall, fromIntDay, toIntDay))
            .filter(sjukfall -> inPeriod(getAllFactsInIntyg(sjukfall.getFirstIntygId(), aisle), fromIntDay, toIntDay))
            .map(sjukfall -> createFkFactRow(sjukfall, aisle));
    }

    private FkFactRow createFkFactRow(Sjukfall sjukfall, Aisle aisle) {
        final String diagnoseClearText = getDiagnoseClearText(sjukfall);
        final Kon kon = sjukfall.getKon();
        final String lanskod = sjukfall.getLanskod();
        final int realDaysFirstIntyg = getRealDaysForIntyg(sjukfall.getFirstIntygId(), aisle);
        return new FkFactRow(diagnoseClearText, kon, lanskod, realDaysFirstIntyg);
    }

    static int getRealDaysForIntyg(long intygId, Aisle aisle) {
        final List<Fact> facts = getAllFactsInIntyg(intygId, aisle).collect(Collectors.toList());
        final HashSet<Integer> dates = new HashSet<>();
        // noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < facts.size(); i++) {
            final Fact fact = facts.get(i);
            for (int j = fact.getStartdatum(); j <= fact.getSlutdatum(); j++) {
                dates.add(j);
            }
        }
        return dates.size();
    }

    private static Stream<Fact> getAllFactsInIntyg(long intygId, Aisle aisle) {
        return StreamSupport.stream(aisle.spliterator(), false)
            .filter(fact -> fact.getLakarintyg() == intygId);
    }

    private String getDiagnoseClearText(Sjukfall sjukfall) {
        String clearTextCode = icd10.findIcd10FromNumericId(sjukfall.getDiagnoskod()).getId();
        if (icd10.findIcd10FromNumericId(sjukfall.getDiagnoskod()).isInternal()) {
            clearTextCode = icd10.findIcd10FromNumericId(sjukfall.getDiagnoskategori()).getId();
        }
        return clearTextCode;
    }

    private boolean inPeriod(Sjukfall sjukfall, int from, int to) {
        return sjukfall.getStart() >= from && sjukfall.getStart() <= to;
    }

    private boolean inPeriod(Stream<Fact> intygFacts, int from, int to) {
        final StartEnd intyg = intygFacts.reduce(new StartEnd(Integer.MAX_VALUE, Integer.MIN_VALUE), (point, fact) -> {
            point.setStart(Math.min(point.getStart(), fact.getStartdatum()));
            point.setEnd(Math.max(point.getEnd(), fact.getSlutdatum()));
            return point;
        }, (point, point2) -> {
            point.setStart(Math.min(point.getStart(), point2.getStart()));
            point.setEnd(Math.max(point.getEnd(), point2.getEnd()));
            return point;
        });

        return (intyg.getStart() <= to && intyg.getStart() >= from) // startsInPeriod
            || (intyg.getEnd() <= to && intyg.getEnd() >= from) // endInPeriod
            || (intyg.getStart() < from && intyg.getEnd() > to); // spansPeriod
    }

    private LocalDate getLastDateOfLastYear() {
        return LocalDate.now(clock).withDayOfYear(1).minusDays(1);
    }

    private LocalDate getFirstDateOfLastYear() {
        return LocalDate.now(clock).withDayOfYear(1).minusYears(1);
    }

    static double roundToTwoDecimals(double number) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat twoDecimalsFormat = new DecimalFormat("0.00", decimalFormatSymbols);
        return Double.valueOf(twoDecimalsFormat.format(number));
    }

    private static class StartEnd {

        private int start;
        private int end;

        StartEnd(int start, int end) {
            this.start = start;
            this.end = end;
        }

        int getStart() {
            return start;
        }

        void setStart(int start) {
            this.start = start;
        }

        int getEnd() {
            return end;
        }

        void setEnd(int end) {
            this.end = end;
        }
    }

}
