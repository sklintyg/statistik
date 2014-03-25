package se.inera.statistics.service.warehouse.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Sjukfall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class DiagnosgruppQuery {

    private static Icd10 icd10;
    private static Map<Integer, Icd10.Kapitel> kapitelMap;

    @Autowired
    public void setIcd10(Icd10 icd10) {
        DiagnosgruppQuery.icd10 = icd10;
        Map<Integer, Icd10.Kapitel> kapitelMap = new HashMap<>();
        for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
            kapitelMap.put(kapitel.toInt(), kapitel);
        }
        DiagnosgruppQuery.kapitelMap = kapitelMap;
    }

    public static List<OverviewChartRowExtended> getOverviewDiagnosgrupper(Collection<Sjukfall> currentSjukfall, Collection<Sjukfall> previousSjukfall, int noOfRows) {
        Map<String, Counter<String>> previousCount = count(previousSjukfall);

        List<Counter<String>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRowExtended> result = new ArrayList<>();

        for (Counter<String> counter : toKeep) {
            int current = counter.getCount();
            int previous = previousCount.get(counter.getKey()).getCount();
            result.add(new OverviewChartRowExtended(counter.getKey(), current, current - previous ));
        }

        return result;
    }

    private static Collection<String> rowsToKeep(Map<String, Counter<String>> count, int noOfRows) {
        List<Counter<String>> sorted = new ArrayList<>();
        for (Counter counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted);

        Collection<String> result = new HashSet<>();
        for (Counter<String> counter : sorted) {
            result.add(counter.getKey());
            if (--noOfRows == 0) {
                break;
            }
        }

        return result;
    }

    public static List<Counter<String>> count(Collection<Sjukfall> sjukfalls, int noOfRows) {
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

    public static Map<String, Counter<String>> count(Collection<Sjukfall> sjukfalls) {
        Map<String, Counter<String>> counters = createCounters();
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(kapitelMap.get(sjukfall.getDiagnoskapitel()).getId());
            counter.increase();
        }
        return counters;
    }

    private static Map<String, Counter<String>> createCounters() {
        Map<String, Counter<String>> counters = new HashMap();
        for (Icd10.Kapitel kapitel : icd10.getKapitel()) {
            counters.put(kapitel.getId(), new Counter(kapitel.getId()));
        }
        return counters;
    }

}
