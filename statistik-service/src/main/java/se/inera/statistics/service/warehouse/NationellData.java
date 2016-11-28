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
package se.inera.statistics.service.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.Counter;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.MessagesQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class NationellData {
    private static final Logger LOG = LoggerFactory.getLogger(NationellData.class);
    public static final int DEFAULT_CUTOFF = 5;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private MessagesQuery messagesQuery;

    @Autowired
    private Lan lans;

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    private int cutoff = DEFAULT_CUTOFF;

    @Autowired
    public void initProperty(@Value("${reports.nationell.cutoff}") int cutoff) {
        final int minimumCutoffValue = 3;
        if (cutoff < minimumCutoffValue) {
            LOG.warn("National cutoff value is too low. Using minimum value: " + minimumCutoffValue);
            this.cutoff = minimumCutoffValue;
            return;
        }
        this.cutoff = cutoff;
    }

    public SimpleKonResponse<SimpleKonDataRow> getMeddelandenPerMonth(Range range) {
        return messagesQuery.getAntalMeddelanden(range.getFrom(), range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(Range range) {
        return getAntalIntyg(range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAntalIntyg(LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (Aisle aisle : warehouse) {
            int index = 0;
            for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, SjukfallUtil.ALL_ENHETER)) {
                int male = SjukfallQuery.countMale(sjukfallGroup.getSjukfall());
                int female = sjukfallGroup.getSjukfall().size() - male;
                String displayDate = ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
                if (index >= result.size()) {
                    result.add(new SimpleKonDataRow(displayDate, filterCutoff(female), filterCutoff(male)));
                } else {
                    SimpleKonDataRow previous = result.get(index);
                    result.set(index, new SimpleKonDataRow(previous.getName(), filterCutoff(female) + previous.getFemale(), filterCutoff(male) + previous.getMale()));
                }
                index++;
            }
        }
        return new SimpleKonResponse<>(result);
    }

    public SimpleKonResponse<SimpleKonDataRow> getHistoricalAgeGroups(Range range) {
        return getAldersgrupper(range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle : warehouse) {
            SimpleKonResponse<SimpleKonDataRow> aldersgrupper = AldersgruppQuery.getAldersgrupper(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = createEmptySimpleKonResponse(aldersgrupper);
            }
            Iterator<SimpleKonDataRow> rowsNew = aldersgrupper.getRows().iterator();
            Iterator<SimpleKonDataRow> rowsOld = result.getRows().iterator();
            List<SimpleKonDataRow> list = new ArrayList<>(perioder);
            while (rowsNew.hasNext() && rowsOld.hasNext()) {
                SimpleKonDataRow a = rowsNew.next();
                SimpleKonDataRow b = rowsOld.next();

                list.add(new SimpleKonDataRow(a.getName(), filterCutoff(a.getFemale()) + b.getFemale(), filterCutoff(a.getMale()) + b.getMale()));
            }
            result = new SimpleKonResponse<>(list);
        }
        if (result == null) {
            return new SimpleKonResponse<>(new ArrayList<>());
        } else {
            return result;
        }
    }

    public KonDataResponse getSjukskrivningsgrad(Range range) {
        return getSjukskrivningsgrad(range.getFrom(), range.getMonths(), 1, false);
    }

    public KonDataResponse getSjukskrivningsgrad(LocalDate start, int perioder, int periodlangd, boolean all) {
        KonDataResponse result = null;
        for (Aisle aisle : warehouse) {
            KonDataResponse grader = SjukskrivningsgradQuery.getSjukskrivningsgrad(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil, all);
            if (result == null) {
                result = createEmptyKonDataResponse(grader);
            }
            Iterator<KonDataRow> rowsNew = grader.getRows().iterator();
            Iterator<KonDataRow> rowsOld = result.getRows().iterator();
            List<KonDataRow> list = new ArrayList<>(perioder);
            while (rowsNew.hasNext() && rowsOld.hasNext()) {
                KonDataRow a = rowsNew.next();
                KonDataRow b = rowsOld.next();

                List<KonField> c = new ArrayList<>();
                for (int i = 0; i < a.getData().size(); i++) {
                    c.add(new KonField(filterCutoff(a.getData().get(i).getFemale()) + b.getData().get(i).getFemale(), filterCutoff(a.getData().get(i).getMale()) + b.getData().get(i).getMale()));
                }
                list.add(new KonDataRow(a.getName(), c));
            }
            result = new KonDataResponse(result.getGroups(), list);
        }
        if (result == null) {
            return new KonDataResponse(new ArrayList<>(), new ArrayList<>());
        } else {
            return result;
        }
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallslangd(Range range) {
        return getSjukfallslangd(range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallslangd(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle : warehouse) {
            SimpleKonResponse<SimpleKonDataRow> langder = SjukskrivningslangdQuery.getSjuksrivningslangd(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = createEmptySimpleKonResponse(langder);
            }
            Iterator<SimpleKonDataRow> rowsNew = langder.getRows().iterator();
            Iterator<SimpleKonDataRow> rowsOld = result.getRows().iterator();
            List<SimpleKonDataRow> list = new ArrayList<>(perioder);
            while (rowsNew.hasNext() && rowsOld.hasNext()) {
                SimpleKonDataRow a = rowsNew.next();
                SimpleKonDataRow b = rowsOld.next();

                list.add(new SimpleKonDataRow(a.getName(), filterCutoff(a.getFemale()) + b.getFemale(), filterCutoff(a.getMale()) + b.getMale()));
            }
            result = new SimpleKonResponse<>(list);
        }
        if (result == null) {
            return new SimpleKonResponse<>(new ArrayList<>());
        } else {
            return result;
        }
    }

    public DiagnosgruppResponse getDiagnosgrupper(Range range) {
        return getDiagnosgrupper(range.getFrom(), range.getMonths(), 1);
    }

    public DiagnosgruppResponse getDiagnosgrupper(LocalDate start, int perioder, int periodlangd) {
        return getDiagnosgrupper(start, perioder, periodlangd, false);
    }

    public DiagnosgruppResponse getDiagnosgrupper(LocalDate start, int perioder, int periodlangd, boolean all) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle : warehouse) {
            DiagnosgruppResponse diagnosgrupper = query.getDiagnosgrupper(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, all);
            if (result == null) {
                result = createEmptyDiagnosgruppResponse(diagnosgrupper);
            }
            Iterator<KonDataRow> rowsNew = diagnosgrupper.getRows().iterator();
            Iterator<KonDataRow> rowsOld = result.getRows().iterator();
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
            final List<? extends Icd> icdTyps = result.getIcdTyps().size() < diagnosgrupper.getIcdTyps().size() ? diagnosgrupper.getIcdTyps() : result.getIcdTyps();
            result = new DiagnosgruppResponse(icdTyps, list);
        }
        if (result == null) {
            return new DiagnosgruppResponse(new ArrayList<>(), new ArrayList<>());
        } else {
            return result;
        }
    }

    private int safeSumForIndex(KonDataRow newRow, KonDataRow existingRow, int index, Kon kon) {
        return filterCutoff(getValueSafe(newRow, index, kon)) + getValueSafe(existingRow, index, kon);
    }

    private int getValueSafe(KonDataRow a, int index, Kon kon) {
        if (a == null || a.getData() == null || a.getData().size() <= index || a.getData().get(index) == null) {
            return 0;
        }
        return a.getData().get(index).getValue(kon);
    }

    public DiagnosgruppResponse getDiagnosavsnitt(Range range, String kapitelId) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle : warehouse) {
            DiagnosgruppResponse diagnosgrupper = query.getDiagnosavsnitts(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), range.getMonths(), 1, kapitelId);
            if (result == null) {
                result = createEmptyDiagnosgruppResponse(diagnosgrupper);
            }
            Iterator<KonDataRow> rowsNew = diagnosgrupper.getRows().iterator();
            Iterator<KonDataRow> rowsOld = result.getRows().iterator();
            List<KonDataRow> list = new ArrayList<>(range.getMonths());
            while (rowsNew.hasNext() && rowsOld.hasNext()) {
                KonDataRow a = rowsNew.next();
                KonDataRow b = rowsOld.next();

                List<KonField> c = new ArrayList<>();
                for (int i = 0; i < a.getData().size(); i++) {
                    c.add(new KonField(filterCutoff(a.getData().get(i).getFemale()) + b.getData().get(i).getFemale(), filterCutoff(a.getData().get(i).getMale()) + b.getData().get(i).getMale()));
                }
                list.add(new KonDataRow(a.getName(), c));
            }
            result = new DiagnosgruppResponse(result.getIcdTyps(), list);
        }
        if (result == null) {
            return new DiagnosgruppResponse(new ArrayList<>(), new ArrayList<>());
        } else {
            return result;
        }
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLan(Range range) {
        return getSjukfallPerLan(range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLan(LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (String lanId : lans) {
            result.add(new SimpleKonDataRow(lans.getNamn(lanId), 0, 0, lanId));
        }
        for (Aisle aisle : warehouse) {
            addAisleToResult(start, perioder, periodlangd, result, aisle);
        }
        return new SimpleKonResponse<>(result);
    }

    private void addAisleToResult(LocalDate start, int perioder, int periodlangd, ArrayList<SimpleKonDataRow> result, Aisle aisle) {
        final Set<String> okandLans = new HashSet<>();

        Map<String, Counter<String>> map = new HashMap<>();
        for (String lanId : lans) {
            map.put(lanId, new Counter<>(lanId));
        }

        for (SjukfallGroup sjukfallGroup : sjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, SjukfallUtil.ALL_ENHETER)) {
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
            result.set(index, new SimpleKonDataRow(previous.getName(), filterCutoff(counter.getCountFemale()) + previous.getFemale(), filterCutoff(counter.getCountMale()) + previous.getMale(), lanId));
            index++;
        }
        if (!okandLans.isEmpty()) {
            LOG.info("Okända län:");
        }
        for (String okandLan : okandLans) {
            LOG.info("Okänt län: " + okandLan);
        }
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukfall(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle : warehouse) {
            SimpleKonResponse<SimpleKonDataRow> langder = SjukskrivningslangdQuery.getLangaSjukfall(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = createEmptySimpleKonResponse(langder);
            }
            Iterator<SimpleKonDataRow> rowsNew = langder.getRows().iterator();
            Iterator<SimpleKonDataRow> rowsOld = result.getRows().iterator();
            List<SimpleKonDataRow> list = new ArrayList<>(perioder);
            while (rowsNew.hasNext() && rowsOld.hasNext()) {
                SimpleKonDataRow a = rowsNew.next();
                SimpleKonDataRow b = rowsOld.next();

                list.add(new SimpleKonDataRow(a.getName(), filterCutoff(a.getFemale()) + b.getFemale(), filterCutoff(a.getMale()) + b.getMale()));
            }
            result = new SimpleKonResponse<>(list);
        }
        if (result == null) {
            return new SimpleKonResponse<>(new ArrayList<>());
        } else {
            return result;
        }
    }

    private int filterCutoff(int actual) {
        return actual < cutoff ? 0 : actual;
    }

    public LocalDateTime getLastUpdate() {
        return warehouse.getLastUpdate();
    }

    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

    private KonDataResponse createEmptyKonDataResponse(KonDataResponse kdr) {
        final ArrayList<KonDataRow> rows = getKonDataRows(kdr);
        return new KonDataResponse(kdr.getGroups(), rows);
    }

    private DiagnosgruppResponse createEmptyDiagnosgruppResponse(DiagnosgruppResponse kdr) {
        final ArrayList<KonDataRow> rows = getKonDataRows(kdr);
        return new DiagnosgruppResponse(kdr.getIcdTyps(), rows);
    }

    private <T extends KonDataResponse> ArrayList<KonDataRow> getKonDataRows(T kdr) {
        final ArrayList<KonDataRow> rows = new ArrayList<>();
        for (KonDataRow row : kdr.getRows()) {
            final ArrayList<KonField> data = new ArrayList<>();
            for (int i = 0; i < row.getData().size(); i++) {
                data.add(new KonField(0, 0));
            }
            rows.add(new KonDataRow(row.getName(), data));
        }
        return rows;
    }

    private SimpleKonResponse<SimpleKonDataRow> createEmptySimpleKonResponse(SimpleKonResponse<SimpleKonDataRow> base) {
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        for (SimpleKonDataRow existingRow : base.getRows()) {
            rows.add(new SimpleKonDataRow(existingRow.getName(), 0, 0));
        }
        return new SimpleKonResponse<>(rows);
    }

}
