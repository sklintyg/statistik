package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.OverviewChartRowExtended;
import se.inera.statistics.service.report.model.OverviewKonsfordelning;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.VerksamhetOverviewResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
public class OverviewQuery {

    private static final int DISPLAYED_AGE_GROUPS = 5;
    public static final int PERCENT = 100;

    @Autowired
    private DiagnosgruppQuery query;

    public VerksamhetOverviewResponse getOverview(Aisle aisle, Predicate<Fact> filter, LocalDate start, int periodlangd) {

        Iterator<SjukfallGroup> groupIterator = SjukfallUtil.sjukfallGrupper(start, 2, periodlangd, aisle, filter).iterator();

        SjukfallGroup previousSjukfall = groupIterator.next();
        SjukfallGroup currentSjukfall = groupIterator.next();

        OverviewKonsfordelning previousKonsfordelning = getOverviewKonsfordelning(previousSjukfall.getRange(), previousSjukfall.getSjukfall());
        OverviewKonsfordelning currentKonsfordelning = getOverviewKonsfordelning(currentSjukfall.getRange(), currentSjukfall.getSjukfall());


        int currentLongSjukfall = SjukfallUtil.getLong(currentSjukfall.getSjukfall());
        int previousLongSjukfall = SjukfallUtil.getLong(previousSjukfall.getSjukfall());

        List<OverviewChartRowExtended> aldersgrupper = AldersgruppQuery.getOverviewAldersgrupper(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall(), DISPLAYED_AGE_GROUPS);
        List<OverviewChartRowExtended> diagnosgrupper = query.getOverviewDiagnosgrupper(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall(), Integer.MAX_VALUE);
        List<OverviewChartRowExtended> sjukskrivningsgrad = SjukskrivningsgradQuery.getOverviewSjukskrivningsgrad(currentSjukfall.getSjukfall(), previousSjukfall.getSjukfall());
        List<OverviewChartRow> sjukskrivningslangd = SjukskrivningslangdQuery.getOverviewSjukskrivningslangd(currentSjukfall.getSjukfall(), Integer.MAX_VALUE);

        return new VerksamhetOverviewResponse(currentSjukfall.getSjukfall().size(), currentKonsfordelning, previousKonsfordelning,
                diagnosgrupper, aldersgrupper, sjukskrivningsgrad, sjukskrivningslangd,
                currentLongSjukfall, percentChange(currentLongSjukfall, previousLongSjukfall));
    }

    private static int percentChange(int current, int previous) {
        if (previous == 0) {
            return 0;
        } else {
            return (current - previous) * PERCENT / previous;
        }
    }

    OverviewKonsfordelning getOverviewKonsfordelning(Range range, Collection<Sjukfall> sjukfalls) {
        int male = countMale(sjukfalls);
        int female = sjukfalls.size() - male;

        return new OverviewKonsfordelning(male, female, range);
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

}
