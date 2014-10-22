package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Collection;

public final class SjukfallQuery {

    private SjukfallQuery() {
    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, Predicate<Fact> filter, LocalDate start, int perioder, int periodlangd) {
        ArrayList<SimpleKonDataRow> result = new ArrayList<>();
        for (SjukfallUtil.SjukfallGroup sjukfallGroup: SjukfallUtil.sjukfallGrupper(start, perioder, periodlangd, aisle, filter)) {
            int male = countMale(sjukfallGroup.getSjukfall());
            int female = sjukfallGroup.getSjukfall().size() - male;
            String displayDate = ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom());
            result.add(new SimpleKonDataRow(displayDate, female, male));
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);

    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfallPerEnhet(Aisle aisle, SjukfallUtil.EnhetFilter filter, Range range, int perioder, int periodlangd) {
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
            if (sjukfall.getKon() == -1) {
                count++;
            }
        }
        return count;
    }

}
