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
package se.inera.testsupport.fkrapport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Predicate;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

/**
 * Creates a flattened multidimensional report of sjukfalls-lengths per diagnos, sex and county.
 */
public class FkReportCreator {
    private static final String ALL_DIAGNOSES_ENTRY = "Alla";
    private static final String MATCH_ANYTHING_REGEXP = "(.*)";
    private Lan lan = new Lan();

    private final Map<HsaIdVardgivare, Aisle> allVardgivare;
    private final SjukfallUtil sjukfallUtil;
    private Icd10 icd10;
    private List<String> diagnoseCategories;
    private Clock clock;

    public FkReportCreator(Map<HsaIdVardgivare, Aisle> allVardgivare, SjukfallUtil sjukfallUtil, Icd10 icd10, List<String> diagnoseCategories, Clock clock) {
        this.allVardgivare = allVardgivare;
        this.sjukfallUtil = sjukfallUtil;
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

    protected List<FkReportDataRow> distributeFactRows(List<FkFactRow> facts, List<FkReportDataRow> results) {
        facts.forEach(fkFactRow -> results.forEach(fkResultRow -> fkResultRow.ingest(fkFactRow)));
        return results;
    }

    protected List<FkReportDataRow> createResultRowsForDiagnoses() {

        // Create an expanded list of all possible diagnose entries to distribute statistics for.
        List<DiagnoseEntry> diagnoseEntries = buildDiagnoseEntries();

        List<FkReportDataRow> rows = new ArrayList<>();

        diagnoseEntries.forEach(diagnoseEntry -> lan.forEach(lansId -> {
            rows.add(new FkReportDataRow(diagnoseEntry.getDiagnoseValue(), diagnoseEntry.getDiagnoseMatcher(), Kon.FEMALE, lansId));
            rows.add(new FkReportDataRow(diagnoseEntry.getDiagnoseValue(), diagnoseEntry.getDiagnoseMatcher(), Kon.MALE, lansId));
        }));

        return rows;
    }

    /**
     * Create the list of DiagnoseEntries that the resultset will be based on.
     *
     * @return The expanded list of all diagnoses to match
     *
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
     * @param diagnoseCategory
     *            The category (e.g "F32") to create matches for
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
        // Dagens dagnr - 5
        final int nowMinusFiveDaysIntDay = WidelineConverter.toDay(LocalDate.now(clock).minusDays(5));

        final ArrayList<FkFactRow> fkFactRows = new ArrayList<>();
        // Ingen filtrering på diagnos eller annat

        final Predicate<Fact> intygFilter = fact -> true;
        // Ingen filtrering på sjukfall
        final FilterPredicates sjukfallFilter = new FilterPredicates(intygFilter, sjukfall -> true, "fkreport");

        // Loopa igenom ALLA facts
        for (Map.Entry<HsaIdVardgivare, Aisle> vgEntry : allVardgivare.entrySet()) {
            // Hämta ut dom som hör till detta år
            final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupperUsingOriginalSjukfallStart(range.getFrom(), 1, range.getMonths(),
                    vgEntry.getValue(), sjukfallFilter);
            for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                    // Om första intyget touchar denna period skall det med (undvik nya där vi kan vara i glappet)
                    if (sjukfall.getStart() <= toIntDay && sjukfall.getEnd() >= fromIntDay && sjukfall.getEnd() < nowMinusFiveDaysIntDay) {
                        String clearTextCode = icd10.findIcd10FromNumericId(sjukfall.getDiagnoskod()).getId();
                        final FkFactRow fkFactRow = new FkFactRow(clearTextCode, sjukfall.getKon(), sjukfall.getLanskod(),
                                sjukfall.getRealDays());
                        fkFactRows.add(fkFactRow);
                    }
                }
            }
        }
        return fkFactRows;
    }

    LocalDate getLastDateOfLastYear() {
        return LocalDate.now(clock).withDayOfYear(1).minusDays(1);
    }

    LocalDate getFirstDateOfLastYear() {
        return LocalDate.now(clock).withDayOfYear(1).minusYears(1);
    }

    static double roundToTwoDecimals(double number) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat twoDecimalsFormat = new DecimalFormat("0.00", decimalFormatSymbols);
        return Double.valueOf(twoDecimalsFormat.format(number));
    }

}
