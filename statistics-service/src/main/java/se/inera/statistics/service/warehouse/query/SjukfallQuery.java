package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
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
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public final class SjukfallQuery {

    @Autowired
    LakareManager lakareManager;

    private SjukfallQuery() {
    }

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

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakare(String vardgivarId, Aisle aisle, SjukfallUtil.EnhetFilter filter, Range range, int perioder, int periodlangd) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        List<Lakare> lakares = lakareManager.getLakares(vardgivarId);
        Multiset<String> femaleSjukfallPerLakare = HashMultiset.create();
        Multiset<String> maleSjukfallPerLakare = HashMultiset.create();
        for (Sjukfall sjukfall : sjukfalls) {
            for (Integer lakarId : sjukfall.getLakare()) {
                Lakare lakare = getLakare(lakares, lakarId);
                if (sjukfall.getKon() == Kon.Female.ordinal()) {
                    femaleSjukfallPerLakare.add(lakarNamn(lakare));
                } else {
                    maleSjukfallPerLakare.add(lakarNamn(lakare));
                }
            }
        }
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (String lakarNamn : Multisets.union(femaleSjukfallPerLakare, maleSjukfallPerLakare).elementSet()) {
            int male = maleSjukfallPerLakare.count(lakarNamn);
            int female = femaleSjukfallPerLakare.count(lakarNamn);
            result.add(new SimpleKonDataRow(lakarNamn, female, male));
        }
        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    private String lakarNamn(Lakare lakare) {
        return lakare.getTilltalsNamn() + " " + lakare.getEfterNamn();
    }

    private Lakare getLakare(List<Lakare> lakares, final Integer lakarId) {
        return Iterables.find(lakares, new Predicate<Lakare>() {
            @Override
            public boolean apply(Lakare lakare) {
                return lakarId == Warehouse.getLakare(lakare.getLakareId());
            }
        });
    }

    public static int countMale(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getKon() == -1) {
                count++;
            }
        }
        return count;
    }

}
