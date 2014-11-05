package se.inera.statistics.service.warehouse.query;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.processlog.Lakare;
import se.inera.statistics.service.processlog.LakareManager;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.*;

import java.util.*;

import static com.google.common.collect.FluentIterable.from;

@Component
public final class SjukfallQuery {

    @Autowired
    LakareManager lakareManager;

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

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakare(String vardgivarId, Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        List<Lakare> allLakaresForVardgivare = lakareManager.getLakares(vardgivarId);
        // Two counters for sjukfall per sex
        final Multiset<Lakare> femaleSjukfallPerLakare = HashMultiset.create();
        final Multiset<Lakare> maleSjukfallPerLakare = HashMultiset.create();

        for (Sjukfall sjukfall : sjukfalls) {
            for (Integer lakarId : sjukfall.getLakare()) {
                Lakare lakare = getLakare(allLakaresForVardgivare, lakarId);
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
    private Set<String> findDuplicates(Set<Lakare> lakares) {
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
