package se.inera.statistics.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.model.SjukfallslangdResponse;
import se.inera.statistics.service.report.model.SjukskrivningsgradResponse;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.report.model.db.SjukfallslangdRow;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.query.AldersgruppQuery;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;
import se.inera.statistics.service.warehouse.query.Counter;
import se.inera.statistics.service.warehouse.query.DiagnosgruppQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningsgradQuery;
import se.inera.statistics.service.warehouse.query.SjukskrivningslangdQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WarehouseService {

    private static final int DISPLAYED_AGE_GROUPS = 7;
    private static final int LONG_SJUKFALL = 90;

    @Autowired
    private Warehouse warehouse;

    @Autowired
    private Icd10 icd10;

    public VerksamhetOverviewResponse getOverview(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        Range previousRange = ReportUtil.getPreviousPeriod(range);
        Iterator<SjukfallUtil.SjukfallGroup> groupIterator = SjukfallUtil.sjukfallGrupper(previousRange.getFrom(), 2, previousRange.getMonths(), aisle, numericalEnhetId).iterator();

        SjukfallUtil.SjukfallGroup previousSjukfall = groupIterator.next();
        SjukfallUtil.SjukfallGroup currentSjukfall = groupIterator.next();

        OverviewKonsfordelning previousKonsfordelning = getOverviewKonsfordelning(previousSjukfall.getRange(), previousSjukfall.getSjukfall());
        OverviewKonsfordelning currentKonsfordelning = getOverviewKonsfordelning(currentSjukfall.getRange(), currentSjukfall.getSjukfall());


        int currentLongSjukfall = SjukfallUtil.getLong(currentSjukfall.getSjukfall());
        int previousLongSjukfall = SjukfallUtil.getLong(previousSjukfall.getSjukfall());

        List<OverviewChartRowExtended> aldersgrupper = AldersgruppQuery.getOverviewAldersgrupper(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall(), DISPLAYED_AGE_GROUPS);
        List<OverviewChartRowExtended> diagnosgrupper = new DiagnosisGroupsConverter().convert(DiagnosgruppQuery.getOverviewDiagnosgrupper(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall(), Integer.MAX_VALUE));
        List<OverviewChartRowExtended> sjukskrivningsgrad = SjukskrivningsgradQuery.getOverviewSjukskrivningsgrad(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall());
        List<OverviewChartRow> sjukskrivningslangd = SjukskrivningslangdQuery.getOverviewSjukskrivningslangd(currentSjukfall.getSjukfall(), Integer.MAX_VALUE);

        return new VerksamhetOverviewResponse(currentSjukfall.getSjukfall().size(), currentKonsfordelning, previousKonsfordelning,
                diagnosgrupper, aldersgrupper, sjukskrivningsgrad, sjukskrivningslangd,
                currentLongSjukfall, currentLongSjukfall - previousLongSjukfall);
    }

    OverviewKonsfordelning getOverviewKonsfordelning(Range range, Collection<Sjukfall> sjukfalls) {
        int male = countMale(sjukfalls);
        int female = sjukfalls.size() - male;

        return new OverviewKonsfordelning(male, female, range);
    }

    private int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == 0) {
                count++;
            }
        }
        return count;
    }

    public SimpleKonResponse<SimpleKonDataRow> getCasesPerMonth(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), range.getMonths(), 1, aisle, numericalEnhetId)) {
            int male = countMale(sjukfallGroup.getSjukfall());
            int female = sjukfallGroup.getSjukfall().size() - male;
            String displayDate = ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
            result.add(new SimpleKonDataRow(displayDate, female, male));
        }

        return new SimpleKonResponse<>(result, range.getMonths());
    }

    public DiagnosgruppResponse getDiagnosgrupperPerMonth(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);
        List<Icd10.Kapitel> kapitel = icd10.getKapitel();

        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), range.getMonths(), 1, aisle, numericalEnhetId)) {
            int[] female = new int[2700];
            int[] male = new int[2700];
            for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {
                if (sjukfall.getKon() == 0) {
                    female[sjukfall.getDiagnoskapitel()]++;
                } else {
                    male[sjukfall.getDiagnoskapitel()]++;
                }
            }
            List<KonField> list = new ArrayList<>(kapitel.size());
            for (Icd10.Kapitel k: kapitel) {
                list.add(new KonField(female[k.toInt()], male[k.toInt()]));
            }
            rows.add(new KonDataRow(ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom()), list));
        }
        List<Avsnitt> avsnitt = new ArrayList<>(kapitel.size());
        for (Icd10.Kapitel k: kapitel) {
            avsnitt.add(new Avsnitt(k.getId(), k.getName()));
        }
        return new DiagnosgruppResponse(avsnitt, rows);
    }

    public static final List<String> GRAD_LABEL = Collections.unmodifiableList(Arrays.asList("25", "50", "75", "100"));
    public static final List<Integer> GRAD = Collections.unmodifiableList(Arrays.asList(25, 50, 75, 100));

    public SjukskrivningsgradResponse getSjukskrivningsgradPerMonth(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), range.getMonths(), 1, aisle, numericalEnhetId)) {
            int[] female = new int[101];
            int[] male = new int[101];
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                if (sjukfall.getKon() == 0) {
                    female[sjukfall.getSjukskrivningsgrad()]++;
                } else {
                    male[sjukfall.getSjukskrivningsgrad()]++;
                }
            }
            List<KonField> list = new ArrayList<>(GRAD.size());
            for (int i: GRAD) {
                list.add(new KonField(female[i], male[i]));
            }
            rows.add(new KonDataRow(ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom()), list));
        }

        return new SjukskrivningsgradResponse(GRAD_LABEL, rows);
    }

    public SjukfallslangdResponse getSjukskrivningslangd(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        List<SjukfallslangdRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), 1, range.getMonths(), aisle, numericalEnhetId)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = SjukskrivningslangdQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : SjukfallslangdUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SjukfallslangdRow("", i.getName(), range.getMonths(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SjukfallslangdResponse(rows, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getLangaSjukskrivningarPerManad(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), range.getMonths(), 1, aisle, numericalEnhetId)) {
            Counter counter = new Counter("");
            for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {
                if (sjukfall.getRealDays() > LONG_SJUKFALL) {
                    counter.increase(sjukfall);
                }
            }
            rows.add(new SimpleKonDataRow(ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom()), counter.getCountFemale(), counter.getCountMale()));
        }

        return new SimpleKonResponse<>(rows, range.getMonths());
    }

    public SimpleKonResponse<SimpleKonDataRow> getAldersgrupper(String enhetId, Range range, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), 1, range.getMonths(), aisle, numericalEnhetId)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = AldersgruppQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : AldersgroupUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }

        return new SimpleKonResponse<>(rows, range.getMonths());
    }

    public DiagnosgruppResponse getDiagnosavsnitt(String enhetId, Range range, String kapitelId, String vardgivarId) {
        Aisle aisle = warehouse.get(vardgivarId);
        int numericalEnhetId = warehouse.getEnhetAndRemember(enhetId);

        Icd10.Kapitel kapitel = icd10.getKapitel(kapitelId);
        List<Avsnitt> avsnitts = new ArrayList<>();
        for (Icd10.Avsnitt avsnitt : kapitel.getAvsnitt()) {
            avsnitts.add(new Avsnitt(avsnitt.getId(), avsnitt.getName()));
        }

        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(range.getFrom(), range.getMonths(), 1, aisle, numericalEnhetId)) {
            int[] female = new int[2700];
            int[] male = new int[2700];
            for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {

                if (sjukfall.getKon() == 0) {
                    female[sjukfall.getDiagnosavsnitt()]++;
                } else {
                    male[sjukfall.getDiagnosavsnitt()]++;
                }
            }

            List<KonField> list = new ArrayList<>(avsnitts.size());
            for (Icd10.Avsnitt avsnitt: kapitel.getAvsnitt()) {
                list.add(new KonField(female[avsnitt.toInt()], male[avsnitt.toInt()]));
            }
            rows.add(new KonDataRow(ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom()), list));
        }


        return new DiagnosgruppResponse(avsnitts, rows);
    }
}
