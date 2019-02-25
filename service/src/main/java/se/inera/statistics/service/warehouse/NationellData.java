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
package se.inera.statistics.service.warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.statistics.service.report.model.AvailableFilters;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.Counter;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import static se.inera.statistics.service.warehouse.ResponseUtil.filterCutoff;

/**
 * Contains calculations for each report on national statistics.
 */
//CHECKSTYLE:OFF ParameterAssignment
class NationellData {
    private static final Logger LOG = LoggerFactory.getLogger(NationellData.class);

    private Lan lans;

    private DiagnosgruppQuery query;

    private SjukfallUtil sjukfallUtil;

    private int cutoff;

    NationellData(Lan lans, DiagnosgruppQuery query, SjukfallUtil sjukfallUtil, int cutoff) {
        this.query = query;
        this.lans = lans;
        this.sjukfallUtil = sjukfallUtil;
        this.cutoff = cutoff;
    }

    SimpleKonDataRow getAntalIntygOverviewResult(Aisle aisle, Range range, SimpleKonDataRow antalIntygResult) {
        final List<SimpleKonDataRow> antalIntygResult1 = new ArrayList<>();
        if (antalIntygResult != null) {
            antalIntygResult1.add(antalIntygResult);
        }
        updateAntalIntygResult(aisle, range.getFrom(), 1, range.getNumberOfMonths(), antalIntygResult1);
        return antalIntygResult1.get(0);
    }

    void updateAntalIntygResult(Aisle aisle, LocalDate start, int perioder, int periodlangd, List<SimpleKonDataRow> antalIntygResult) {
        int index = 0;
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle,
                SjukfallUtil.ALL_ENHETER)) {
            int male = SjukfallQuery.countMale(sjukfallGroup.getSjukfall());
            int female = sjukfallGroup.getSjukfall().size() - male;
            String displayDate = ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
            if (index >= antalIntygResult.size()) {
                antalIntygResult.add(new SimpleKonDataRow(displayDate, filterCutoff(female, cutoff), filterCutoff(male, cutoff)));
            } else {
                SimpleKonDataRow previous = antalIntygResult.get(index);
                antalIntygResult.set(index, new SimpleKonDataRow(previous.getName(),
                        filterCutoff(female, cutoff) + previous.getFemale(),
                        filterCutoff(male, cutoff) + previous.getMale()));
            }
            index++;
        }
    }

    SimpleKonResponse getAldersgrupper(Aisle aisle, Range range, SimpleKonResponse aldersgrupperResult, Ranges ranges) {
        SimpleKonResponse aldersgrupper = AldersgruppQuery.getAldersgrupper(aisle,
                SjukfallUtil.ALL_ENHETER, range.getFrom(), 1, range.getNumberOfMonths(), sjukfallUtil, ranges);
        if (aldersgrupperResult == null) {
            aldersgrupperResult = createEmptySimpleKonResponse(aldersgrupper);
        }
        Iterator<SimpleKonDataRow> rowsNew = aldersgrupper.getRows().iterator();
        Iterator<SimpleKonDataRow> rowsOld = aldersgrupperResult.getRows().iterator();
        List<SimpleKonDataRow> list = new ArrayList<>(1);
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            SimpleKonDataRow a = rowsNew.next();
            SimpleKonDataRow b = rowsOld.next();

            list.add(new SimpleKonDataRow(a.getName(), filterCutoff(a.getFemale(), cutoff) + b.getFemale(),
                    filterCutoff(a.getMale(), cutoff) + b.getMale()));
        }
        return new SimpleKonResponse(AvailableFilters.getForNationell(), list);
    }

    KonDataResponse getSjukskrivningsgrad(Aisle aisle, Range range, KonDataResponse sjukskrivningsgradResult) {
        return getSjukskrivningsgrad(aisle, range.getFrom(), range.getNumberOfMonths(), 1, false, sjukskrivningsgradResult);
    }

    KonDataResponse getSjukskrivningsgradOverview(Aisle aisle, Range range, KonDataResponse sjukskrivningsgradResult) {
        return getSjukskrivningsgrad(aisle, range.getFrom(), 1, range.getNumberOfMonths(), true, sjukskrivningsgradResult);
    }

    private KonDataResponse getSjukskrivningsgrad(Aisle aisle, LocalDate start, int perioder, int periodlangd,
                                          boolean all, KonDataResponse sjukskrivningsgradResult) {
        KonDataResponse grader = SjukskrivningsgradQuery.getSjukskrivningsgrad(aisle,
                SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil, all);
        if (sjukskrivningsgradResult == null) {
            sjukskrivningsgradResult = ResponseUtil.createEmptyKonDataResponse(grader);
        }
        Iterator<KonDataRow> rowsNew = grader.getRows().iterator();
        Iterator<KonDataRow> rowsOld = sjukskrivningsgradResult.getRows().iterator();
        List<KonDataRow> list = ResponseUtil.getKonDataRows(perioder, rowsNew, rowsOld, cutoff);
        return new KonDataResponse(AvailableFilters.getForNationell(), sjukskrivningsgradResult.getGroups(), list);
    }

    SimpleKonResponse getSjukfallslangd(Aisle aisle, Range range, SimpleKonResponse sjukfallslangdResult) {
        SimpleKonResponse langder = SjukskrivningslangdQuery.getSjuksrivningslangd(
                aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), 1, range.getNumberOfMonths(), sjukfallUtil);
        if (sjukfallslangdResult == null) {
            sjukfallslangdResult = createEmptySimpleKonResponse(langder);
        }
        Iterator<SimpleKonDataRow> rowsNew = langder.getRows().iterator();
        Iterator<SimpleKonDataRow> rowsOld = sjukfallslangdResult.getRows().iterator();
        List<SimpleKonDataRow> list = new ArrayList<>(1);
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            SimpleKonDataRow a = rowsNew.next();
            SimpleKonDataRow b = rowsOld.next();
            final int female = filterCutoff(a.getFemale(), cutoff) + b.getFemale();
            final int male = filterCutoff(a.getMale(), cutoff) + b.getMale();
            list.add(new SimpleKonDataRow(a.getName(), female, male));
        }
        return new SimpleKonResponse(AvailableFilters.getForNationell(), list);
    }

    DiagnosgruppResponse getDiagnosgrupperOverview(Aisle aisle, Range range, DiagnosgruppResponse diagnosgrupperResult) {
        return getDiagnosgrupper(aisle, range.getFrom(), 1, range.getNumberOfMonths(), true, diagnosgrupperResult);
    }

    DiagnosgruppResponse getDiagnosgrupper(Aisle aisle, Range range, DiagnosgruppResponse diagnosgrupperResult) {
        return getDiagnosgrupper(aisle, range.getFrom(), range.getNumberOfMonths(), 1, false, diagnosgrupperResult);
    }

    private DiagnosgruppResponse getDiagnosgrupper(Aisle aisle, LocalDate start, int perioder, int periodlangd,
                                           boolean all, DiagnosgruppResponse diagnosgrupperResult) {
        final FilterPredicates filter = SjukfallUtil.ALL_ENHETER;
        DiagnosgruppResponse diagnosgrupper = query.getDiagnosgrupper(aisle, filter, start, perioder, periodlangd, all);
        if (diagnosgrupperResult == null) {
            diagnosgrupperResult = ResponseUtil.createEmptyDiagnosgruppResponse(diagnosgrupper);
        }
        Iterator<KonDataRow> rowsNew = diagnosgrupper.getRows().iterator();
        Iterator<KonDataRow> rowsOld = diagnosgrupperResult.getRows().iterator();
        List<KonDataRow> list = new ArrayList<>(perioder);
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            KonDataRow a = rowsNew.next();
            KonDataRow b = rowsOld.next();

            List<KonField> c = new ArrayList<>();
            final int maxDataSize = Math.max(a.getData().size(), b.getData().size());
            for (int i = 0; i < maxDataSize; i++) {
                c.add(new KonField(safeSumForIndex(a, b, i, Kon.FEMALE), safeSumForIndex(a, b, i, Kon.MALE)));
            }
            list.add(new KonDataRow(a.getName(), c));
        }
        final List<? extends Icd> icdTyps = diagnosgrupperResult.getIcdTyps().size() < diagnosgrupper.getIcdTyps().size()
                ? diagnosgrupper.getIcdTyps() : diagnosgrupperResult.getIcdTyps();
        return new DiagnosgruppResponse(AvailableFilters.getForNationell(), icdTyps, list);
    }

    private int safeSumForIndex(KonDataRow newRow, KonDataRow existingRow, int index, Kon kon) {
        return filterCutoff(getValueSafe(newRow, index, kon), cutoff) + getValueSafe(existingRow, index, kon);
    }

    private int getValueSafe(KonDataRow a, int index, Kon kon) {
        if (a == null || a.getData() == null || a.getData().size() <= index || a.getData().get(index) == null) {
            return 0;
        }
        return a.getData().get(index).getValue(kon);
    }

    void getDiagnosavsnitt(Aisle aisle, Range range, Icd10.Kapitel kapitel, Map<Icd10.Kapitel, DiagnosgruppResponse> diagnosavsnitts) {
        DiagnosgruppResponse diagnosgrupper = query.getDiagnosavsnitts(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(),
                range.getNumberOfMonths(), 1, kapitel.getId());
        if (!diagnosavsnitts.containsKey(kapitel)) {
            diagnosavsnitts.put(kapitel, ResponseUtil.createEmptyDiagnosgruppResponse(diagnosgrupper));
        }
        Iterator<KonDataRow> rowsNew = diagnosgrupper.getRows().iterator();
        Iterator<KonDataRow> rowsOld = diagnosavsnitts.get(kapitel).getRows().iterator();
        List<KonDataRow> list = new ArrayList<>(range.getNumberOfMonths());
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            KonDataRow a = rowsNew.next();
            KonDataRow b = rowsOld.next();

            List<KonField> c = new ArrayList<>();
            for (int i = 0; i < a.getData().size(); i++) {
                final int female = filterCutoff(a.getData().get(i).getFemale(), cutoff) + b.getData().get(i).getFemale();
                final int male = filterCutoff(a.getData().get(i).getMale(), cutoff) + b.getData().get(i).getMale();
                c.add(new KonField(female, male));
            }
            list.add(new KonDataRow(a.getName(), c));
        }

        DiagnosgruppResponse response = new DiagnosgruppResponse(AvailableFilters.getForNationell(),
                diagnosavsnitts.get(kapitel).getIcdTyps(), list);
        diagnosavsnitts.put(kapitel, response);
    }

    void addSjukfallPerLanToResult(Range range, ArrayList<SimpleKonDataRow> result, Aisle aisle) {
        final Set<String> okandLans = new HashSet<>();

        Map<String, Counter<String>> map = new HashMap<>();
        for (String lanId : lans) {
            map.put(lanId, new Counter<>(lanId));
        }

        final FilterPredicates filter = SjukfallUtil.ALL_ENHETER;
        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(range.getFrom(), 1, range.getNumberOfMonths(), aisle, filter)) {
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                Counter counter = map.get(sjukfall.getLanskod());
                if (counter != null) {
                    counter.increase(sjukfall);
                } else {
                    map.get(Lan.OVRIGT_ID).increase(sjukfall);
                    okandLans.add(sjukfall.getLanskod());
                }
            }
        }
        int index = 0;
        for (String lanId : lans) {
            Counter<String> counter = map.get(lanId);
            SimpleKonDataRow previous = result.get(index);
            final int female = filterCutoff(counter.getCountFemale(), cutoff) + previous.getFemale();
            final int male = filterCutoff(counter.getCountMale(), cutoff) + previous.getMale();
            result.set(index, new SimpleKonDataRow(previous.getName(), female, male, lanId));
            index++;
        }
        if (!okandLans.isEmpty()) {
            LOG.info("Okända län:");
        }
        for (String okandLan : okandLans) {
            LOG.info("Okänt län: " + okandLan);
        }
    }

    SimpleKonResponse getLangaSjukfall(Aisle aisle, Range range, SimpleKonResponse langaResult) {
        SimpleKonResponse langder = SjukskrivningslangdQuery.getLangaSjukfall(aisle,
                SjukfallUtil.ALL_ENHETER, range.getFrom(), 1, range.getNumberOfMonths(), sjukfallUtil);
        if (langaResult == null) {
            langaResult = createEmptySimpleKonResponse(langder);
        }
        Iterator<SimpleKonDataRow> rowsNew = langder.getRows().iterator();
        Iterator<SimpleKonDataRow> rowsOld = langaResult.getRows().iterator();
        List<SimpleKonDataRow> list = new ArrayList<>(1);
        while (rowsNew.hasNext() && rowsOld.hasNext()) {
            SimpleKonDataRow a = rowsNew.next();
            SimpleKonDataRow b = rowsOld.next();

            list.add(new SimpleKonDataRow(a.getName(), filterCutoff(a.getFemale(), cutoff) + b.getFemale(),
                    filterCutoff(a.getMale(), cutoff) + b.getMale()));
        }
        return new SimpleKonResponse(AvailableFilters.getForNationell(), list);
    }

    private SimpleKonResponse createEmptySimpleKonResponse(SimpleKonResponse base) {
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        for (SimpleKonDataRow existingRow : base.getRows()) {
            rows.add(new SimpleKonDataRow(existingRow.getName(), 0, 0));
        }
        return new SimpleKonResponse(AvailableFilters.getForNationell(), rows);
    }

}
