/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukfallslangdRow;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.Counter;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

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

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Lan lans;

    private Set<String> okandLans = new HashSet<>();

    @Autowired
    private DiagnosgruppQuery query;

    @Autowired
    private SjukfallUtil sjukfallUtil;

    @Value("${reports.nationell.cutoff:5}")
    private int cutoff;

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
                    result.add(new SimpleKonDataRow(displayDate, female, male));
                } else {
                    SimpleKonDataRow previous = result.get(index);
                    result.set(index, new SimpleKonDataRow(previous.getName(), female + previous.getFemale(), male + previous.getMale()));
                }
                index++;
            }
        }
        return filterLow(new SimpleKonResponse<>(result, perioder * periodlangd));
    }

    public SimpleKonResponse<SimpleKonDataRow> getHistoricalAgeGroups(Range range) {
        return filterLow(getAldersgrupper(range.getFrom(), 1, range.getMonths()));
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle : warehouse) {
            SimpleKonResponse<SimpleKonDataRow> aldersgrupper = AldersgruppQuery.getAldersgrupper(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = aldersgrupper;
            } else {
                Iterator<SimpleKonDataRow> rowsNew = aldersgrupper.getRows().iterator();
                Iterator<SimpleKonDataRow> rowsOld = result.getRows().iterator();
                List<SimpleKonDataRow> list = new ArrayList<>(perioder);
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    SimpleKonDataRow a = rowsNew.next();
                    SimpleKonDataRow b = rowsOld.next();

                    list.add(new SimpleKonDataRow(a.getName(), a.getFemale() + b.getFemale(), a.getMale() + b.getMale()));
                }
                result = new SimpleKonResponse<>(list, perioder * periodlangd);
            }
        }
        if (result == null) {
            return new SimpleKonResponse<>(new ArrayList<SimpleKonDataRow>(), 0);
        } else {
            return filterLow(result);
        }
    }

    public SjukskrivningsgradResponse getSjukskrivningsgrad(Range range) {
        return getSjukskrivningsgrad(range.getFrom(), range.getMonths(), 1);
    }

    public SjukskrivningsgradResponse getSjukskrivningsgrad(LocalDate start, int perioder, int periodlangd) {
        SjukskrivningsgradResponse result = null;
        for (Aisle aisle : warehouse) {
            SjukskrivningsgradResponse grader = SjukskrivningsgradQuery.getSjukskrivningsgrad(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = grader;
            } else {
                Iterator<KonDataRow> rowsNew = grader.getRows().iterator();
                Iterator<KonDataRow> rowsOld = result.getRows().iterator();
                List<KonDataRow> list = new ArrayList<>(perioder);
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    KonDataRow a = rowsNew.next();
                    KonDataRow b = rowsOld.next();

                    List<KonField> c = new ArrayList<>();
                    for (int i = 0; i < a.getData().size(); i++) {
                        c.add(new KonField(a.getData().get(i).getFemale() + b.getData().get(i).getFemale(), a.getData().get(i).getMale() + b.getData().get(i).getMale()));
                    }
                    list.add(new KonDataRow(a.getName(), c));
                }
                result = new SjukskrivningsgradResponse(result.getGroups(), list);
            }
        }
        if (result == null) {
            return new SjukskrivningsgradResponse(new ArrayList<String>(), new ArrayList<KonDataRow>());
        } else {
            return filterLow(result);
        }
    }

    public SjukfallslangdResponse getSjukfallslangd(Range range) {
        return getSjukfallslangd(range.getFrom(), 1, range.getMonths());
    }

    public SjukfallslangdResponse getSjukfallslangd(LocalDate start, int perioder, int periodlangd) {
        SjukfallslangdResponse result = null;
        for (Aisle aisle : warehouse) {
            SjukfallslangdResponse langder = SjukskrivningslangdQuery.getSjuksrivningslangd(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = langder;
            } else {
                Iterator<SjukfallslangdRow> rowsNew = langder.getRows().iterator();
                Iterator<SjukfallslangdRow> rowsOld = result.getRows().iterator();
                List<SjukfallslangdRow> list = new ArrayList<>(perioder);
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    SjukfallslangdRow a = rowsNew.next();
                    SjukfallslangdRow b = rowsOld.next();

                    list.add(new SjukfallslangdRow(a.getGroup(), a.getFemale() + b.getFemale(), a.getMale() + b.getMale()));
                }
                result = new SjukfallslangdResponse(list, perioder * periodlangd);
            }
        }
        if (result == null) {
            return new SjukfallslangdResponse(new ArrayList<SjukfallslangdRow>(), 0);
        } else {
            return filterLow(result);
        }
    }

    public DiagnosgruppResponse getDiagnosgrupper(Range range) {
        return filterLow(getDiagnosgrupper(range.getFrom(), range.getMonths(), 1));
    }

    public DiagnosgruppResponse getDiagnosgrupper(LocalDate start, int perioder, int periodlangd) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle : warehouse) {
            DiagnosgruppResponse diagnosgrupper = query.getDiagnosgrupper(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd);

            if (result == null) {
                result = diagnosgrupper;
            } else {
                Iterator<KonDataRow> rowsNew = diagnosgrupper.getRows().iterator();
                Iterator<KonDataRow> rowsOld = result.getRows().iterator();
                List<KonDataRow> list = new ArrayList<>(perioder);
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    KonDataRow a = rowsNew.next();
                    KonDataRow b = rowsOld.next();

                    List<KonField> c = new ArrayList<>();
                    for (int i = 0; i < a.getData().size(); i++) {
                        c.add(new KonField(a.getData().get(i).getFemale() + b.getData().get(i).getFemale(), a.getData().get(i).getMale() + b.getData().get(i).getMale()));
                    }
                    list.add(new KonDataRow(a.getName(), c));
                }
                result = new DiagnosgruppResponse(result.getIcdTyps(), list);
            }
        }
        if (result == null) {
            return new DiagnosgruppResponse(new ArrayList<Icd>(), new ArrayList<KonDataRow>());
        } else {
            return filterLow(result);
        }
    }

    public DiagnosgruppResponse getDiagnosavsnitt(Range range, String kapitelId) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle : warehouse) {
            DiagnosgruppResponse diagnosgrupper = query.getDiagnosavsnitts(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), range.getMonths(), 1, kapitelId);

            if (result == null) {
                result = diagnosgrupper;
            } else {
                Iterator<KonDataRow> rowsNew = diagnosgrupper.getRows().iterator();
                Iterator<KonDataRow> rowsOld = result.getRows().iterator();
                List<KonDataRow> list = new ArrayList<>(range.getMonths());
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    KonDataRow a = rowsNew.next();
                    KonDataRow b = rowsOld.next();

                    List<KonField> c = new ArrayList<>();
                    for (int i = 0; i < a.getData().size(); i++) {
                        c.add(new KonField(a.getData().get(i).getFemale() + b.getData().get(i).getFemale(), a.getData().get(i).getMale() + b.getData().get(i).getMale()));
                    }
                    list.add(new KonDataRow(a.getName(), c));
                }
                result = new DiagnosgruppResponse(result.getIcdTyps(), list);
            }
        }
        if (result == null) {
            return new DiagnosgruppResponse(new ArrayList<Icd>(), new ArrayList<KonDataRow>());
        } else {
            return filterLow(result);
        }
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLan(Range range) {
        return filterLow(getSjukfallPerLan(range.getFrom(), 1, range.getMonths()));
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLan(LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (String lanId : lans) {
            result.add(new SimpleKonDataRow(lans.getNamn(lanId), 0, 0));
        }
        for (Aisle aisle : warehouse) {
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
                result.set(index, new SimpleKonDataRow(previous.getName(), counter.getCountFemale() + previous.getFemale(), counter.getCountMale() + previous.getMale()));
                index++;
            }
            if (!okandLans.isEmpty()) {
                LOG.info("Okända län:");
            }
            for (String okandLan : okandLans) {
                LOG.info("Okänt län: " + okandLan);
            }
        }
        return filterLow(new SimpleKonResponse<>(result, perioder * periodlangd));
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukfall(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle : warehouse) {
            SimpleKonResponse<SimpleKonDataRow> langder = SjukskrivningslangdQuery.getLangaSjukfall(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd, sjukfallUtil);
            if (result == null) {
                result = langder;
            } else {
                Iterator<SimpleKonDataRow> rowsNew = langder.getRows().iterator();
                Iterator<SimpleKonDataRow> rowsOld = result.getRows().iterator();
                List<SimpleKonDataRow> list = new ArrayList<>(perioder);
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    SimpleKonDataRow a = rowsNew.next();
                    SimpleKonDataRow b = rowsOld.next();

                    list.add(new SimpleKonDataRow(a.getName(), a.getFemale() + b.getFemale(), a.getMale() + b.getMale()));
                }
                result = new SimpleKonResponse<>(list, perioder * periodlangd);
            }
        }
        if (result == null) {
            return new SimpleKonResponse<>(new ArrayList<SimpleKonDataRow>(), 0);
        } else {
            return filterLow(result);
        }
    }

    private SjukfallslangdResponse filterLow(SjukfallslangdResponse unfiltered) {
        if (unfiltered == null) {
            return null;
        }
        List<SjukfallslangdRow> rows = new ArrayList<>();
        for (SjukfallslangdRow row : unfiltered.getRows()) {
            rows.add(new SjukfallslangdRow(row.getGroup(), filterCutoff(row.getFemale()), filterCutoff(row.getMale())));
        }
        return new SjukfallslangdResponse(rows, unfiltered.getMonths());
    }

    private SjukskrivningsgradResponse filterLow(SjukskrivningsgradResponse unfiltered) {
        List<KonDataRow> rows = new ArrayList<>();
        for (KonDataRow row : unfiltered.getRows()) {
            rows.add(new KonDataRow(row.getName(), filterLowKonFields(row.getData())));
        }
        return new SjukskrivningsgradResponse(unfiltered.getGroups(), rows);
    }

    private DiagnosgruppResponse filterLow(DiagnosgruppResponse unfiltered) {
        List<KonDataRow> rows = new ArrayList<>();
        for (KonDataRow r : unfiltered.getRows()) {
            rows.add(new KonDataRow(r.getName(), filterLowKonFields(r.getData())));
        }
        return new DiagnosgruppResponse(unfiltered.getIcdTyps(), rows);
    }

    private List<KonField> filterLowKonFields(List<KonField> unfiltered) {
        List<KonField> result = new ArrayList<>();
        for (KonField field : unfiltered) {
            result.add(new KonField(filterCutoff(field.getFemale()), filterCutoff(field.getMale())));
        }
        return result;
    }

    private SimpleKonResponse<SimpleKonDataRow> filterLow(SimpleKonResponse<SimpleKonDataRow> unfiltered) {
        return new SimpleKonResponse<>(filterLow(unfiltered.getRows()), unfiltered.getNumberOfMonthsCalculated());
    }

    private List<SimpleKonDataRow> filterLow(List<SimpleKonDataRow> unfiltered) {
        List<SimpleKonDataRow> result = new ArrayList<>();
        for (SimpleKonDataRow row : unfiltered) {
            result.add(new SimpleKonDataRow(row.getName(), filterCutoff(row.getFemale()), filterCutoff(row.getMale())));
        }
        return result;
    }

    private int filterCutoff(int actual) {
        return actual < cutoff ? 0 : actual;
    }

    public org.joda.time.LocalDateTime getLastUpdate() {
        return warehouse.getLastUpdate();
    }

    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

}
