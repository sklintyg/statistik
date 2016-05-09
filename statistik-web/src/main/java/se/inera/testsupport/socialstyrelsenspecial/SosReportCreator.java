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
package se.inera.testsupport.socialstyrelsenspecial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.WidelineConverter;

import com.google.common.base.Predicate;

public class SosReportCreator {

    private final Map<HsaIdVardgivare, Aisle> allVardgivare;
    private final SjukfallUtil sjukfallUtil;

    public SosReportCreator(Map<HsaIdVardgivare, Aisle> allVardgivare, SjukfallUtil sjukfallUtil) {
        this.allVardgivare = allVardgivare;
        this.sjukfallUtil = sjukfallUtil;
    }

    public List<SosRow> getSosReport() {
        final HashMap<String, Integer> mymap = new HashMap<>();
        mymap.put("F32", Icd10.icd10ToInt("F32", Icd10RangeType.KATEGORI));
        mymap.put("F33", Icd10.icd10ToInt("F33", Icd10RangeType.KATEGORI));
        mymap.put("F410", Icd10.icd10ToInt("F410", Icd10RangeType.KOD));
        mymap.put("F411", Icd10.icd10ToInt("F411", Icd10RangeType.KOD));
        mymap.put("F430", Icd10.icd10ToInt("F430", Icd10RangeType.KOD));
        mymap.put("F432", Icd10.icd10ToInt("F432", Icd10RangeType.KOD));
        mymap.put("F438", Icd10.icd10ToInt("F438", Icd10RangeType.KOD));
        mymap.put("F438A", Icd10.icd10ToInt("F438A", Icd10RangeType.KOD));
        mymap.put("F438W", Icd10.icd10ToInt("F438W", Icd10RangeType.KOD));

        final LocalDate from = LocalDate.parse("2015-01-01");
        final Range range = new Range(from, LocalDate.now());
        final int fromIntDay = WidelineConverter.toDay(from);
        final int toIntDay = WidelineConverter.toDay(LocalDate.parse("2015-12-31"));
        final int nowMinusFiveDaysIntDay = WidelineConverter.toDay(LocalDate.now().minusDays(5));

        final ArrayList<SosRow> sosRows = new ArrayList<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : mymap.entrySet()) {
            final Integer dx = stringIntegerEntry.getValue();
            final String dxString = stringIntegerEntry.getKey();
            final SjukfallFilter sjukfallFilter = new SjukfallFilter(new Predicate<Fact>() {
                @Override
                public boolean apply(Fact fact) {
                    return fact.getDiagnoskod() == dx || fact.getDiagnoskategori() == dx;
                }
            }, "sosspecial" + dx);

            for (Map.Entry<HsaIdVardgivare, Aisle> vgEntry : allVardgivare.entrySet()) {
                final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupperUsingOriginalSjukfallStart(range.getFrom(), 1, range.getMonths(),
                        vgEntry.getValue(), sjukfallFilter);
                for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
                    for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                        if (sjukfall.getStart() <= toIntDay && sjukfall.getEnd() >= fromIntDay && sjukfall.getEnd() < nowMinusFiveDaysIntDay) {
                            final SosRow sosRow = new SosRow(dxString, sjukfall.getKon(), sjukfall.getLanskod(), sjukfall.getRealDays());
                            sosRows.add(sosRow);
                        }
                    }
                }
            }
        }
        return sosRows;
    }

    private List<SosCalculatedRow> getCalculatedValuesSosReport(Function<List<SosRow>, Double> calcFunc) {
        final ArrayList<SosCalculatedRow> sosCalculatedRows = new ArrayList<>();
        final List<SosRow> sosReport = getSosReport();
        final Map<String, List<SosRow>> rowsByDiagnos = sosReport.stream().collect(Collectors.groupingBy(SosRow::getDiagnos));
        for (Map.Entry<String, List<SosRow>> dxEntry : rowsByDiagnos.entrySet()) {
            final Map<Kon, List<SosRow>> dxrowsByKon = dxEntry.getValue().stream().collect(Collectors.groupingBy(SosRow::getKon));


            final double total = calcFunc.apply(dxEntry.getValue());
            final double female = calcFunc.apply(dxrowsByKon.get(Kon.Female));
            final double male = calcFunc.apply(dxrowsByKon.get(Kon.Male));

            final Map<String, List<SosRow>> dxrowsByLan = dxEntry.getValue().stream().collect(Collectors.groupingBy(SosRow::getLanId));
            final HashMap<String, Number> lanNumbers = new HashMap<>();
            for (Map.Entry<String, List<SosRow>> lanEntry : dxrowsByLan.entrySet()) {
                final String lanId = lanEntry.getKey();
                final double valueForLan = calcFunc.apply(lanEntry.getValue());
                lanNumbers.put(lanId, valueForLan);
            }

            sosCalculatedRows.add(new SosCalculatedRow(dxEntry.getKey(), total, female, male, lanNumbers));
        }
        return sosCalculatedRows;
    }

    public List<SosCalculatedRow> getMedianValuesSosReport() {
        return getCalculatedValuesSosReport(value -> {
            final List<Double> totalValues = getDoubles(value);
            return new MathStatistics(totalValues).median();
        });
    }

    public List<SosCalculatedRow> getStdDevValuesSosReport() {
        return getCalculatedValuesSosReport(value -> {
            final List<Double> totalValues = getDoubles(value);
            return new MathStatistics(totalValues).getStdDev();
        });
    }

    private List<Double> getDoubles(List<SosRow> value) {
        return value.stream().map(sosRow -> (double) sosRow.getLength()).collect(Collectors.toList());
    }

}
