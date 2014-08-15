package se.inera.statistics.web.service;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.db.SjukfallslangdRow;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NationellData {

    @Autowired
    private Warehouse warehouse;

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(Range range) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (Aisle aisle: warehouse) {

            int index = 0;
            for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), range.getMonths(), 1, aisle, SjukfallUtil.ALL_ENHETER)) {
                int male = WarehouseService.countMale(sjukfallGroup.getSjukfall());
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
        return new SimpleKonResponse<>(result, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getHistoricalAgeGroups(LocalDate localDate, Range range) {
        SimpleKonResponse<SimpleKonDataRow> result = null;
        for (Aisle aisle: warehouse) {
            SimpleKonResponse<SimpleKonDataRow> aldersgrupper = AldersgruppQuery.getAldersgrupper(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), 1, range.getMonths());
            if (result == null) {
                result = aldersgrupper;
            } else {
                Iterator<SimpleKonDataRow> rowsNew = aldersgrupper.getRows().iterator();
                Iterator<SimpleKonDataRow> rowsOld = result.getRows().iterator();
                List<SimpleKonDataRow> list = new ArrayList<>(range.getMonths());
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    SimpleKonDataRow a = rowsNew.next();
                    SimpleKonDataRow b = rowsOld.next();

                    list.add(new SimpleKonDataRow(a.getName(), a.getFemale() + b.getFemale(), a.getMale() + b.getMale()));
                }
                result = new SimpleKonResponse<>(list, range.getMonths());
            }
        }
        return result;
    }

    public SjukskrivningsgradResponse getSjukskrivningsgrad(Range range) {
        SjukskrivningsgradResponse result = null;
        for (Aisle aisle: warehouse) {
            SjukskrivningsgradResponse grader = SjukskrivningsgradQuery.getSjukskrivningsgrad(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), range.getMonths(), 1);
            if (result == null) {
                result = grader;
            } else {
                Iterator<KonDataRow> rowsNew = grader.getRows().iterator();
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
                result = new SjukskrivningsgradResponse(result.getDegreesOfSickLeave(), list);
            }
        }
        return result;
    }

    public SjukfallslangdResponse getSjukfallslangd(Range range) {
        SjukfallslangdResponse result = null;
        for (Aisle aisle: warehouse) {
            SjukfallslangdResponse langder = SjukskrivningslangdQuery.getSjuksrivningslangd(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), 1, range.getMonths());
            if (result == null) {
                result = langder;
            } else {
                Iterator<SjukfallslangdRow> rowsNew = langder.getRows().iterator();
                Iterator<SjukfallslangdRow> rowsOld = result.getRows().iterator();
                List<SjukfallslangdRow> list = new ArrayList<>(range.getMonths());
                while (rowsNew.hasNext() && rowsOld.hasNext()) {
                    SjukfallslangdRow a = rowsNew.next();
                    SjukfallslangdRow b = rowsOld.next();

                    list.add(new SjukfallslangdRow(a.getPeriod(), a.getGroup(), a.getKey().getPeriods(), a.getFemale() + b.getFemale(), a.getMale() + b.getMale()));
                }
                result = new SjukfallslangdResponse(list, range.getMonths());
            }
        }
        return result;
    }

    public DiagnosgruppResponse getDiagnosgrupper(Range range) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle: warehouse) {
            DiagnosgruppResponse diagnosgrupper = DiagnosgruppQuery.getDiagnosgrupper(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), range.getMonths(), 1);

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

    public DiagnosgruppResponse getDiagnosavsnitt(Range range, String kapitelId) {
        DiagnosgruppResponse result = null;
        for (Aisle aisle: warehouse) {
            DiagnosgruppResponse diagnosgrupper = DiagnosgruppQuery.getDiagnosavsnitt(aisle, SjukfallUtil.ALL_ENHETER, range.getFrom(), range.getMonths(), 1, kapitelId);

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
}
