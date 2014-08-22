package se.inera.statistics.service.warehouse;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.db.SjukfallslangdRow;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.Counter;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.SjukfallQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class NationellData {

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Lan lans;

    @Autowired
    private DiagnosgruppQuery query;

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(Range range) {
        return getAntalIntyg(range.getFrom(), range.getMonths(), 1);
    }

    public SimpleKonResponse<SimpleKonDataRow> getAntalIntyg(LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (Aisle aisle: warehouse) {
            int index = 0;
            for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, SjukfallUtil.ALL_ENHETER)) {
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
        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    public SimpleKonResponse<SimpleKonDataRow> getHistoricalAgeGroups(Range range) {
        return getAldersgrupper(range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle: warehouse) {
            SimpleKonResponse<SimpleKonDataRow> aldersgrupper = AldersgruppQuery.getAldersgrupper(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd);
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
        return result;
    }

    public SjukskrivningsgradResponse getSjukskrivningsgrad(Range range) {
        return getSjukskrivningsgrad(range.getFrom(), range.getMonths(), 1);
    }

    public SjukskrivningsgradResponse getSjukskrivningsgrad(LocalDate start, int perioder, int periodlangd) {
        SjukskrivningsgradResponse result = null;
        for (Aisle aisle: warehouse) {
            SjukskrivningsgradResponse grader = SjukskrivningsgradQuery.getSjukskrivningsgrad(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd);
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
                result = new SjukskrivningsgradResponse(result.getDegreesOfSickLeave(), list);
            }
        }
        return result;
    }

    public SjukfallslangdResponse getSjukfallslangd(Range range) {
        return getSjukfallslangd(range.getFrom(), 1, range.getMonths());
    }

    public SjukfallslangdResponse getSjukfallslangd(LocalDate start, int perioder, int periodlangd) {
        SjukfallslangdResponse result = null;
        for (Aisle aisle: warehouse) {
            SjukfallslangdResponse langder = SjukskrivningslangdQuery.getSjuksrivningslangd(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd);
            if (result == null) {
                result = langder;
            } else {
                Iterator<SjukfallslangdRow> rowsNew = langder.getRows().iterator();
                Iterator<SjukfallslangdRow> rowsOld = result.getRows().iterator();
                List<SjukfallslangdRow> list = new ArrayList<>(perioder);
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    SjukfallslangdRow a = rowsNew.next();
                    SjukfallslangdRow b = rowsOld.next();

                    list.add(new SjukfallslangdRow(a.getPeriod(), a.getGroup(), a.getKey().getPeriods(), a.getFemale() + b.getFemale(), a.getMale() + b.getMale()));
                }
                result = new SjukfallslangdResponse(list, perioder * periodlangd);
            }
        }
        return result;
    }

    public DiagnosgruppResponse getDiagnosgrupper(Range range) {
        return getDiagnosgrupper(range.getFrom(), range.getMonths(), 1);
    }

    public DiagnosgruppResponse getDiagnosgrupper(LocalDate start, int perioder, int periodlangd) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle: warehouse) {
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
                result = new DiagnosgruppResponse(result.getAvsnitts(), list);
            }
        }
        return result;
    }

    public DiagnosgruppResponse getDiagnosavsnitt(Range range, String kapitelId) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle: warehouse) {
            DiagnosgruppResponse diagnosgrupper = query.getDiagnosavsnitt(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), range.getMonths(), 1, kapitelId);

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
                result = new DiagnosgruppResponse(result.getAvsnitts(), list);
            }
        }
        return result;
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLan(Range range) {
        return getSjukfallPerLan(range.getFrom(), 1, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLan(LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (String lanId : lans) {
            result.add(new SimpleKonDataRow(lans.getNamn(lanId), 0, 0));
        }
        for (Aisle aisle: warehouse) {
            Map<String, Counter<String>> map = new HashMap<>();
            for (String lanId : lans) {
                map.put(lanId, new Counter<>(lanId));
            }

            for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, SjukfallUtil.ALL_ENHETER)) {
                for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {
                    map.get(sjukfall.getLanskod()).increase(sjukfall);
                }
            }
            int index = 0;
            for (String lanId : lans) {
                Counter<String> counter = map.get(lanId);
                SimpleKonDataRow previous = result.get(index);
                result.set(index, new SimpleKonDataRow(previous.getName(), counter.getCountFemale() + previous.getFemale(), counter.getCountMale() + previous.getMale()));
                index++;
            }
        }
        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukfall(LocalDate start, int perioder, int periodlangd) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle: warehouse) {
            SimpleKonResponse<SimpleKonDataRow> langder = SjukskrivningslangdQuery.getLangaSjukfall(aisle, SjukfallUtil.ALL_ENHETER, start, perioder, periodlangd);
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
        return result;

    }
}
