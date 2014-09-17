package se.inera.statistics.service.warehouse.query;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.Avsnitt;
import se.inera.statistics.service.report.model.DiagnosgruppResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class DiagnosgruppQuery {

    public static final int MAX_DIAGNOS_ID = 15000;

    @Autowired
    private Icd10 icd10;

    private Map<Integer, Icd10.Kapitel> kapitelMap;

    @PostConstruct
    public void parseIcd10() {
        kapitelMap = new HashMap<>();
        for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
            kapitelMap.put(kapitel.toInt(), kapitel);
        }
    }

    public List<OverviewChartRowExtended> getOverviewDiagnosgrupper(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall, int noOfRows) {
        Map<String, Counter<String>> previousCount = count(previousSjukfall);

        List<Counter<String>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Counter<String> counter : toKeep) {
            int current = counter.getCount();
            int previous = previousCount.get(counter.getKey()).getCount();
            result.add(new OverviewChartRowExtended(counter.getKey(), current, current - previous));
        }

        return result;
    }

    public DiagnosgruppResponse getDiagnosgrupper(Aisle aisle, SjukfallUtil.StartFilter filter, LocalDate start, int periods, int periodLength) {
        List<Icd10.Kapitel> kapitel = icd10.getKapitel();

        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(start, periods, periodLength, aisle, filter)) {
            int[] female = new int[MAX_DIAGNOS_ID];
            int[] male = new int[MAX_DIAGNOS_ID];
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
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }
        List<Avsnitt> avsnitt = new ArrayList<>(kapitel.size());
        for (Icd10.Kapitel k: kapitel) {
            avsnitt.add(new Avsnitt(k.getId(), k.getName()));
        }
        return new DiagnosgruppResponse(avsnitt, rows);
    }

    public DiagnosgruppResponse getDiagnosavsnitt(Aisle aisle, SjukfallUtil.StartFilter filter, LocalDate start, int periods, int periodLength, String kapitelId) {
        Icd10.Kapitel kapitel = icd10.getKapitel(kapitelId);
        List<Avsnitt> avsnitts = new ArrayList<>();
        for (Icd10.Avsnitt avsnitt : kapitel.getAvsnitt()) {
            avsnitts.add(new Avsnitt(avsnitt.getId(), avsnitt.getName()));
        }

        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(start, periods, periodLength, aisle, filter)) {
            int[] female = new int[MAX_DIAGNOS_ID];
            int[] male = new int[MAX_DIAGNOS_ID];
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
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }


        return new DiagnosgruppResponse(avsnitts, rows);
    }

    private static Collection<String> rowsToKeep(Map<String, Counter<String>> count, int noOfRows) {
        List<Counter<String>> sorted = new ArrayList<>();
        for (Counter<String> counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted);

        Collection<String> result = new HashSet<>();
        for (Counter<String> counter : sorted) {
            result.add(counter.getKey());
            if (result.size() == noOfRows) {
                break;
            }
        }

        return result;
    }

    public List<Counter<String>> count(Collection<Sjukfall> sjukfalls, int noOfRows) {
        Map<String, Counter<String>> map = count(sjukfalls);
        List<Counter<String>> result = new ArrayList<>();

        Collection<String> rowsToKeep = rowsToKeep(map, noOfRows);
        for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
            if (rowsToKeep.contains(kapitel.getId())) {
                result.add(map.get(kapitel.getId()));
            }
        }

        return result;
    }

    public Map<String, Counter<String>> count(Collection<Sjukfall> sjukfalls) {
        Map<String, Counter<String>> counters = createCounters();
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(kapitelMap.get(sjukfall.getDiagnoskapitel()).getId());
            counter.increase(sjukfall);
        }
        return counters;
    }

    private Map<String, Counter<String>> createCounters() {
        Map<String, Counter<String>> counters = new HashMap<>();
        for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
            counters.put(kapitel.getId(), new Counter<>(kapitel.getId()));
        }
        return counters;
    }

}
