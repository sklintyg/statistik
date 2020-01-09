/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Diagnos;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

public class SosReportCreator {

    private final Iterator<Aisle> aisles;
    private final SjukfallUtil sjukfallUtil;
    private final boolean startWithSpecifiedDxs;
    private final Icd10 icd10;
    private HashMap<String, Integer> dxsToShowInReport = new HashMap<>();
    private Clock clock;
    private int fromYear;
    private int toYear;

    /**
     * @param dxStrings diagnosis to generate report for, if null then use default dxs
     * @param fromYear From which year to calculate
     * @param toYear To which year to calculate
     */
    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    public SosReportCreator(Iterator<Aisle> aisles, SjukfallUtil sjukfallUtil, Icd10 icd10, List<String> dxStrings,
        boolean startWithSpecifiedDxs, Clock clock, int fromYear, int toYear) {
        this.aisles = aisles;
        this.sjukfallUtil = sjukfallUtil;
        this.startWithSpecifiedDxs = startWithSpecifiedDxs;
        this.clock = clock;
        if (dxStrings == null || dxStrings.isEmpty()) {
            populateDefaultDxs();
        } else {
            for (String dxString : dxStrings) {
                Icd10.Id dx = icd10.findFromIcd10Code(dxString);
                dxsToShowInReport.put(dx.getId(), dx.toInt());
            }
        }
        this.fromYear = fromYear;
        this.toYear = toYear;
        this.icd10 = icd10;
    }

    private void populateDefaultDxs() {
        dxsToShowInReport.put("F32", Icd10.icd10ToInt("F32", Icd10RangeType.KATEGORI));
        dxsToShowInReport.put("F33", Icd10.icd10ToInt("F33", Icd10RangeType.KATEGORI));
        dxsToShowInReport.put("F410", Icd10.icd10ToInt("F410", Icd10RangeType.KOD));
        dxsToShowInReport.put("F411", Icd10.icd10ToInt("F411", Icd10RangeType.KOD));
        dxsToShowInReport.put("F430", Icd10.icd10ToInt("F430", Icd10RangeType.KOD));
        dxsToShowInReport.put("F432", Icd10.icd10ToInt("F432", Icd10RangeType.KOD));
        dxsToShowInReport.put("F438", Icd10.icd10ToInt("F438", Icd10RangeType.KOD));
        dxsToShowInReport.put("F438A", Icd10.icd10ToInt("F438A", Icd10RangeType.KOD));
        dxsToShowInReport.put("F438W", Icd10.icd10ToInt("F438W", Icd10RangeType.KOD));
    }

    public List<SosRow> getSosReport() {
        final LocalDate from = getFirstDateOfYear();
        final Range range = new Range(from, LocalDate.now(clock));
        final int fromIntDay = WidelineConverter.toDay(from);
        final int toIntDay = WidelineConverter.toDay(getLastDateOfYear());
        final int nowMinusFiveDaysIntDay = WidelineConverter.toDay(LocalDate.now(clock).minusDays(5));

        final ArrayList<SosRow> sosRows = new ArrayList<>();
        for (Iterator<Aisle> it = aisles; it.hasNext();) {
            Aisle aisle = it.next();
            for (Map.Entry<String, Integer> stringIntegerEntry : dxsToShowInReport.entrySet()) {
                final Integer dx = stringIntegerEntry.getValue();
                final String dxString = stringIntegerEntry.getKey();
                final Predicate<Fact> intygFilter = startWithSpecifiedDxs ? fact -> true
                    : fact -> fact.getDiagnoskod() == dx || fact.getDiagnoskategori() == dx;
                final FilterPredicates sjukfallFilter = new FilterPredicates(intygFilter, sjukfall -> true,
                    "sosspecial" + fromYear + dx + toYear, false);

                final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(range.getFrom(), 1,
                    range.getNumberOfMonths(), aisle, sjukfallFilter);
                for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                    for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                        if (sjukfall.getStart() <= toIntDay && sjukfall.getEnd() >= fromIntDay
                            && sjukfall.getEnd() < nowMinusFiveDaysIntDay) {
                            final Kon kon = sjukfall.getKon();
                            final String lanskod = sjukfall.getLanskod();
                            final int realDays = sjukfall.getRealDays();
                            if (startWithSpecifiedDxs) {
                                if (dxMatching(sjukfall.getFirstDx(), dx)) {
                                    final Diagnos lastDx = sjukfall.getLastDx();
                                    final int dxKat = lastDx.getDiagnoskategori();
                                    final Icd10.Id icd10Id = icd10.findIcd10FromNumericId(dxKat);
                                    final SosRow sosRow = new SosRow(icd10Id.getVisibleId(), kon, lanskod, realDays);
                                    sosRows.add(sosRow);
                                }
                            } else {
                                final SosRow sosRow = new SosRow(dxString, kon, lanskod, realDays);
                                sosRows.add(sosRow);
                            }
                        }
                    }
                }
            }
        }
        return sosRows;
    }

    private boolean dxMatching(Diagnos dx, Integer dxToMatch) {
        return dxToMatch != null
            && (dx.getDiagnosavsnitt() == dxToMatch || dx.getDiagnoskapitel() == dxToMatch
            || dx.getDiagnoskategori() == dxToMatch || dx.getDiagnoskod() == dxToMatch);
    }

    LocalDate getLastDateOfYear() {
        return LocalDate.now(clock).withYear(toYear + 1).withDayOfYear(1).minusDays(1);
    }

    LocalDate getFirstDateOfYear() {
        return LocalDate.now(clock).withDayOfYear(1).withYear(fromYear);
    }

    private List<SosCalculatedRow> getCalculatedValuesSosReport(Function<List<SosRow>, Double> calcFunc) {
        final ArrayList<SosCalculatedRow> sosCalculatedRows = new ArrayList<>();
        final List<SosRow> sosReport = getSosReport();
        final Map<String, List<SosRow>> rowsByDiagnos = sosReport.stream().collect(Collectors.groupingBy(SosRow::getDiagnos));
        for (Map.Entry<String, List<SosRow>> dxEntry : rowsByDiagnos.entrySet()) {
            final Map<Kon, List<SosRow>> dxrowsByKon = dxEntry.getValue().stream().collect(Collectors.groupingBy(SosRow::getKon));

            final Double total = calcFunc.apply(dxEntry.getValue());
            final Double female = calcFunc.apply(dxrowsByKon.get(Kon.FEMALE));
            final Double male = calcFunc.apply(dxrowsByKon.get(Kon.MALE));

            final Map<String, List<SosRow>> dxrowsByLan = dxEntry.getValue().stream().collect(Collectors.groupingBy(SosRow::getLanId));
            final HashMap<String, Number> lanNumbers = new HashMap<>();
            for (Map.Entry<String, List<SosRow>> lanEntry : dxrowsByLan.entrySet()) {
                final String lanId = lanEntry.getKey();
                final Double valueForLan = calcFunc.apply(lanEntry.getValue());
                lanNumbers.put(lanId, valueForLan);
            }

            sosCalculatedRows.add(new SosCalculatedRow(dxEntry.getKey(), total, female, male, lanNumbers));
        }
        return sosCalculatedRows;
    }

    public List<SosCalculatedRow> getMedianValuesSosReport() {
        return getCalculatedValuesSosReport(value -> {
            if (value == null) {
                return null;
            }
            final List<Double> totalValues = getDoubles(value);
            return new MathStatistics(totalValues).median();
        });
    }

    public List<SosCalculatedRow> getStdDevValuesSosReport() {
        return getCalculatedValuesSosReport(value -> {
            if (value == null) {
                return null;
            }
            final List<Double> totalValues = getDoubles(value);
            return new MathStatistics(totalValues).getStdDev();
        });
    }

    private List<Double> getDoubles(List<SosRow> value) {
        return value.stream().map(sosRow -> (double) sosRow.getLength()).collect(Collectors.toList());
    }

}
