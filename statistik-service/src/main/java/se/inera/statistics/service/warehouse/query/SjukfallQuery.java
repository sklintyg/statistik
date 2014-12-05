package se.inera.statistics.service.warehouse.query;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static se.inera.statistics.service.report.util.Ranges.range;

@Component
public final class SjukfallQuery {

    public static final Ranges RANGES_LAKARES_ALDER_OCH_KON = new Ranges(range("under 30 år", 30), range("30-39 år", 40), range("40-49 år", 50), range("50-59 år", 60), range("över 60 år", Integer.MAX_VALUE));

    @Autowired
    private LakareManager lakareManager;

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, Predicate<Fact> filter, LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup : SjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, filter)) {
            int male = countMale(sjukfallGroup.getSjukfall());
            int female = sjukfallGroup.getSjukfall().size() - male;
            String displayDate = ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
            result.add(new SimpleKonDataRow(displayDate, female, male));
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerEnhet(Aisle aisle, SjukfallUtil.EnhetFilter filter, Range range, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (int enhetId : filter.getEnhetIds()) {
            Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, new SjukfallUtil.EnhetFilter(enhetId));
            int male = countMale(sjukfalls);
            int female = sjukfalls.size() - male;
            result.add(new SimpleKonDataRow(filter.getEnhetsName(enhetId), female, male));
        }
        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    public static int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == Kon.Male) {
                count++;
            }
        }
        return count;
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakaresAlderOchKon(Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        final Multimap<Kon, String> lakarenAlderOchKonForSjukfalls = getLakarensAlderOchKonForAllSjukfall(aisle, filter, range);
        List<SimpleKonDataRow> result = new ArrayList<>();
        final Iterator<Ranges.Range> ranges = RANGES_LAKARES_ALDER_OCH_KON.iterator();
        while (ranges.hasNext()) {
            Ranges.Range ageRange = ranges.next();
            for (Kon kon : Kon.values()) {
                final int femaleCount = Collections.frequency(lakarenAlderOchKonForSjukfalls.get(Kon.Female), String.valueOf(ageRange.getCutoff()) + kon);
                final int maleCount = Collections.frequency(lakarenAlderOchKonForSjukfalls.get(Kon.Male), String.valueOf(ageRange.getCutoff()) + kon);
                if (femaleCount + maleCount > 0) {
                    result.add(new SimpleKonDataRow(getLakareAlderOchKonTitle(kon, ageRange), femaleCount, maleCount));
                }
            }
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    private String getLakareAlderOchKonTitle(Kon kon, Ranges.Range ageRange) {
        return getLakareAlderOchKonTitleKonPart(kon) + " " + ageRange.getName();
    }

    private String getLakareAlderOchKonTitleKonPart(Kon kon) {
        switch (kon) {
            case Female: return "Kvinnlig läkare";
            case Male: return "Manlig läkare";
            case Unknown: return "";
            default: throw new IllegalArgumentException("Unhandled type: " + kon);
        }
    }

    private Multimap<Kon, String> getLakarensAlderOchKonForAllSjukfall(Aisle aisle, Predicate<Fact> filter, Range range) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        final Multimap<Kon, String> sjukfallPerLakare = ArrayListMultimap.create();
        for (Sjukfall sjukfall : sjukfalls) {
            for (se.inera.statistics.service.warehouse.Lakare lakare : sjukfall.getLakare()) {
                final Ranges.Range ageRange = RANGES_LAKARES_ALDER_OCH_KON.rangeFor(lakare.getAge());
                sjukfallPerLakare.put(sjukfall.getKon(), String.valueOf(ageRange.getCutoff()) + lakare.getKon());
            }
        }
        return sjukfallPerLakare;
    }

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakare(String vardgivarId, Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        List<Lakare> allLakaresForVardgivare = lakareManager.getLakares(vardgivarId);
        // Two counters for sjukfall per sex
        final Multiset<Lakare> femaleSjukfallPerLakare = HashMultiset.create();
        final Multiset<Lakare> maleSjukfallPerLakare = HashMultiset.create();

        for (Sjukfall sjukfall : sjukfalls) {
            for (se.inera.statistics.service.warehouse.Lakare warehousLakare : sjukfall.getLakare()) {
                Lakare lakare = getLakare(allLakaresForVardgivare, warehousLakare.getId());
                if (lakare != null) {
                    if (sjukfall.getKon() == Kon.Female) {
                        femaleSjukfallPerLakare.add(lakare);
                    } else {
                        maleSjukfallPerLakare.add(lakare);
                    }
                }
            }
        }

        // All lakares who have male or female sjukfalls
        Set<Lakare> allLakaresWithSjukfall = Multisets.union(femaleSjukfallPerLakare, maleSjukfallPerLakare).elementSet();
        final Set<String> duplicateNames = findDuplicates(allLakaresWithSjukfall);

        List<SimpleKonDataRow> result = new ArrayList<>();
        for (Lakare lakare : allLakaresWithSjukfall) {
            String lakarNamn = lakarNamn(lakare);
            if (duplicateNames.contains(lakarNamn)) {
                lakarNamn = lakarNamn + " " + lakare.getLakareId();
            }
            result.add(new SimpleKonDataRow(lakarNamn, femaleSjukfallPerLakare.count(lakare), maleSjukfallPerLakare.count(lakare)));
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    // Collect a list of all "läkar-namn" that exist more than once in the set of lakare
    private Set<String> findDuplicates(Collection<Lakare> lakares) {
        Set<String> duplicates = new HashSet<>();
        Set<String> seenLakarNames = new HashSet<>();
        for (Lakare lakare : lakares) {
            String lakarNamn = lakarNamn(lakare);
            if (seenLakarNames.contains(lakarNamn)) {
                duplicates.add(lakarNamn);
            }
            seenLakarNames.add(lakarNamn);
        }
        return duplicates;
    }

    private String lakarNamn(Lakare lakare) {
        return lakare.getTilltalsNamn() + " " + lakare.getEfterNamn();
    }

    private Lakare getLakare(List<Lakare> lakares, final Integer lakarId) {
        return Iterables.find(lakares, new Predicate<Lakare>() {
            @Override
            public boolean apply(Lakare lakare) {
                return lakarId == Warehouse.getNumLakarId(lakare.getLakareId());
            }
        }, null);
    }

    @VisibleForTesting
    public void setLakareManager(LakareManager lakareManager) {
        this.lakareManager = lakareManager;
    }

}
