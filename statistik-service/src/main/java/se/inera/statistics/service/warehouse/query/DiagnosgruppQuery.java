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
package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Icd10RangeType;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DiagnosgruppQuery {

    @Autowired
    private Icd10 icd10;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    public List<OverviewChartRowExtended> getOverviewDiagnosgrupper(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall, int noOfRows) {
        Map<Integer, Counter<Integer>> previousCount = count(previousSjukfall);

        List<Counter<Integer>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Counter<Integer> counter : toKeep) {
            int current = counter.getCount();
            final Counter<Integer> previousCounter = previousCount.get(counter.getKey());
            int previous = previousCounter == null ? 0 : previousCounter.getCount();
            final Icd10.Id icd10 = this.icd10.findIcd10FromNumericId(counter.getKey());
            result.add(new OverviewChartRowExtended(String.valueOf(icd10.toInt()), current, current - previous));
        }

        return result;
    }

    public DiagnosgruppResponse getDiagnosgrupper(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, boolean countAllDxs) {
        List<Icd10.Kapitel> kapitel = icd10.getKapitel(true);
        final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(start, periods, periodLength, aisle, filter);
        List<KonDataRow> rows = getKonDataRows(sjukfallGroups, kapitel, Icd10RangeType.KAPITEL, countAllDxs);
        removeEmptyInternalIcd10Groups(kapitel, rows);
        List<Icd> avsnitt = new ArrayList<>(kapitel.size());
        for (Icd10.Kapitel k: kapitel) {
            avsnitt.add(new Icd(k.getVisibleId(), k.getName(), k.toInt()));
        }
        return new DiagnosgruppResponse(avsnitt, rows);
    }

    public DiagnosgruppResponse getDiagnosgrupper(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength) {
        return getDiagnosgrupper(aisle, filter, start, periods, periodLength, false);
    }

    private void removeEmptyInternalIcd10Groups(List<Icd10.Kapitel> kapitel, List<KonDataRow> rows) {
        int indexOfEmptyInternalIcd10Group = getIndexOfEmptyInternalIcd10Group(kapitel, rows);
        while (indexOfEmptyInternalIcd10Group >= 0) {
            removeGroupWithIndex(indexOfEmptyInternalIcd10Group, kapitel, rows);
            indexOfEmptyInternalIcd10Group = getIndexOfEmptyInternalIcd10Group(kapitel, rows);
        }
    }

    private void removeGroupWithIndex(int index, List<Icd10.Kapitel> kapitel, List<KonDataRow> rows) {
        kapitel.remove(index);
        for (KonDataRow row : rows) {
            row.getData().remove(index);
        }
    }

    private int getIndexOfEmptyInternalIcd10Group(List<Icd10.Kapitel> kapitels, List<KonDataRow> rows) {
        for (int i = 0; i < kapitels.size(); i++) {
            final Icd10.Kapitel kapitel = kapitels.get(i);
            if (kapitel.isInternal()) {
                int sum = 0;
                for (KonDataRow row : rows) {
                    final KonField konField = row.getData().get(i);
                    sum += konField.getFemale() + konField.getMale();
                }
                if (sum == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, String rangeId) throws RangeNotFoundException {
        return getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, rangeId, false);
    }

    public DiagnosgruppResponse getUnderdiagnosgrupper(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, String rangeId, boolean countAllDxs) throws RangeNotFoundException {
        final Icd10.Kapitel kapitel = icd10.getKapitel(rangeId);
        if (kapitel != null) {
            return getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, kapitel, Icd10RangeType.AVSNITT, countAllDxs);
        }

        final Icd10.Avsnitt avsnitt = icd10.getAvsnitt(rangeId);
        if (avsnitt != null) {
            return getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, avsnitt, Icd10RangeType.KATEGORI, countAllDxs);
        }

        final Icd10.Kategori kategori = icd10.getKategori(rangeId);
        if (kategori != null) {
            return getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, kategori, Icd10RangeType.KOD, countAllDxs);
        }

        throw new RangeNotFoundException("Could not find ICD10 range: " + rangeId);
    }

    public SimpleKonResponse<SimpleKonDataRow> getJamforDiagnoser(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, List<String> diagnosis) {
        final List<Icd10.Id> kategoris = Lists.transform(diagnosis, new Function<String, Icd10.Id>() {
            @Override
            public Icd10.Id apply(String diagnos) {
                return icd10.findIcd10FromNumericId(Integer.valueOf(diagnos));
            }
        });
        final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(start, periods, periodLength, aisle, filter);
        final List<KonDataRow> periodRows = getKonDataRows(sjukfallGroups, kategoris, Icd10RangeType.KATEGORI, true);
        final List<SimpleKonDataRow> rows = new ArrayList<>(kategoris.size());
        final List<KonField> data = periodRows.get(0).getData();
        for (int i = 0; i < data.size(); i++) {
            final KonField row = data.get(i);
            final Icd10.Id kategori = kategoris.get(i);
            rows.add(new SimpleKonDataRow((kategori.getVisibleId() + " " + kategori.getName()).trim(), row));
        }
        return new SimpleKonResponse<>(rows);
    }

    public KonDataResponse getJamforDiagnoserTidsserie(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, List<String> diagnosis) {
        final List<Icd10.Id> kategoris = Lists.transform(diagnosis, new Function<String, Icd10.Id>() {
            @Override
            public Icd10.Id apply(String diagnos) {
                return icd10.findIcd10FromNumericId(Integer.valueOf(diagnos));
            }
        });
        final List<String> names = Lists.transform(kategoris, new Function<Icd10.Id, String>() {
            @Override
            public String apply(Icd10.Id id) {
                return (id.getVisibleId() + " " + id.getName()).trim();
            }
        });
        final List<Integer> ids = Lists.transform(kategoris, new Function<Icd10.Id, Integer>() {
            @Override
            public Integer apply(Icd10.Id id) {
                return id.toInt();
            }
        });
        final CounterFunction<Integer> counterFunction = new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                counter.add(sjukfall.getDiagnoskategori());
            }
        };

        return sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }
    // CHECKSTYLE:OFF ParameterNumber
    @java.lang.SuppressWarnings("squid:S00107") // Suppress parameter number warning in Sonar
    private DiagnosgruppResponse getUnderdiagnosgrupper(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, Icd10.Range kapitel, Icd10RangeType rangeType, boolean countAllDxs) {
        final List<Icd> icdTyps = new ArrayList<>();
        for (Icd10.Id icdItem : kapitel.getSubItems()) {
            icdTyps.add(new Icd(icdItem.getVisibleId(), icdItem.getName(), icdItem.toInt()));
        }
        final Iterable<SjukfallGroup> sjukfallGroups = sjukfallUtil.sjukfallGrupper(start, periods, periodLength, aisle, filter);
        final List<KonDataRow> rows = getKonDataRows(sjukfallGroups, kapitel.getSubItems(), rangeType, countAllDxs);
        return new DiagnosgruppResponse(icdTyps, rows);
    }
    // CHECKSTYLE:ON ParameterNumber

    private DiagnosgruppResponse getUnderdiagnosgrupper(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, Icd10.Range kapitel, Icd10RangeType rangeType) {
        return getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, kapitel, rangeType, false);
    }

    private List<KonDataRow> getKonDataRows(Iterable<SjukfallGroup> sjukfallGroups, List<? extends Icd10.Id> kapitel, Icd10RangeType rangeType, boolean countAllDxs) {
        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: sjukfallGroups) {
            Map<Integer, Integer> female = new HashMap<>();
            Map<Integer, Integer> male = new HashMap<>();
            for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {
                final Set<Integer> icd10Codes = new HashSet<>(countAllDxs ? sjukfall.getAllIcd10OfType(rangeType) : Arrays.asList(sjukfall.getIcd10CodeForType(rangeType)));
                final Map<Integer, Integer> genderMap = sjukfall.getKon() == Kon.FEMALE ? female : male;
                for (Integer icd10Code : icd10Codes) {
                    final int currentCount = getCurrentCount(icd10Code, genderMap);
                    genderMap.put(icd10Code, currentCount + 1);
                }
            }

            List<KonField> list = new ArrayList<>(kapitel.size());
            for (Icd10.Id icdItem : kapitel) {
                list.add(new KonField(getCurrentCount(icdItem.toInt(), female), getCurrentCount(icdItem.toInt(), male)));
            }
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }
        return rows;
    }

    private int getCurrentCount(int icd10Code, Map<Integer, Integer> genderMap) {
        final Integer integer = genderMap.get(icd10Code);
        if (integer != null) {
            return integer;
        }
        return 0;
    }

    public DiagnosgruppResponse getDiagnosavsnitts(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, String kapitelId) {
        Icd10.Kapitel kapitel = icd10.getKapitel(kapitelId);
        return getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, kapitel, Icd10RangeType.AVSNITT);
    }

    private static <T> Collection<T> rowsToKeep(Map<T, Counter<T>> count, int noOfRows) {
        List<Counter<T>> sorted = new ArrayList<>();
        for (Counter<T> counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted, Counter.byTotalCount());

        Collection<T> result = new ArrayList<>();
        for (Counter<T> counter : sorted) {
            result.add(counter.getKey());
            if (result.size() == noOfRows) {
                break;
            }
        }
        return result;
    }

    public List<Counter<Integer>> count(Collection<Sjukfall> sjukfalls, int noOfRows) {
        Map<Integer, Counter<Integer>> map = count(sjukfalls);
        List<Counter<Integer>> result = new ArrayList<>();
        Collection<Integer> rowsToKeep = rowsToKeep(map, noOfRows);
        for (Integer intId : rowsToKeep) {
            result.add(map.get(intId));
        }
        return result;
    }

    public Map<Integer, Counter<Integer>> count(Collection<Sjukfall> sjukfalls) {
        Map<Integer, Counter<Integer>> counters = new HashMap<>();
        for (Sjukfall sjukfall : sjukfalls) {
            sjukfall.getDiagnoskapitels().distinct().forEach(d -> {
                Counter<Integer> counter = counters.get(d);
                if (counter == null) {
                    counter = new Counter<>(d);
                    counters.put(d, counter);
                }
                counter.increase(sjukfall);
            });
        }
        return counters;
    }

    public SimpleKonResponse<SimpleKonDataRow> getDiagnosgrupperTvarsnitt(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength) {
        final DiagnosgruppResponse diagnosgruppResponse = getDiagnosgrupper(aisle, filter, start, periods, periodLength, true);
        return SimpleKonResponse.create(diagnosgruppResponse);
    }

    public SimpleKonResponse<SimpleKonDataRow> getUnderdiagnosgrupperTvarsnitt(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, String rangeId) throws RangeNotFoundException {
        final DiagnosgruppResponse underdiagnosgrupper = getUnderdiagnosgrupper(aisle, filter, start, periods, periodLength, rangeId, true);
        return SimpleKonResponse.create(underdiagnosgrupper);
    }
}
