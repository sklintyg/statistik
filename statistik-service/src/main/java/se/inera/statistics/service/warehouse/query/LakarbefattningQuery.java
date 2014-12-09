package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LakarbefattningQuery {

    private static final Map<Integer, String> LAKARBEFATTNINGS = new LinkedHashMap<>();

    static {
        // CHECKSTYLE:OFF MagicNumber
        LAKARBEFATTNINGS.put(201010, "Överläkare");
        LAKARBEFATTNINGS.put(201011, "Distriktsläkare/Specialist allmänmedicin");
        LAKARBEFATTNINGS.put(201012, "Skolläkare");
        LAKARBEFATTNINGS.put(201013, "Företagsläkare");
        LAKARBEFATTNINGS.put(202010, "Specialistläkare");
        LAKARBEFATTNINGS.put(203010, "Läkare legitimerad, specialiseringstjänstgöring");
        LAKARBEFATTNINGS.put(203090, "Läkare legitimerad, annan");
        LAKARBEFATTNINGS.put(204010, "Läkare ej legitimerad, allmäntjänstgöring");
        LAKARBEFATTNINGS.put(204090, "Läkare ej legitimerad, annan");
        // CHECKSTYLE:ON MagicNumber
    }

    private LakarbefattningQuery() {
    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        return new LakarbefattningQuery().getSjukfallResponse(aisle, filter, range, perioder, periodlangd);
    }

    private SimpleKonResponse<SimpleKonDataRow> getSjukfallResponse(Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        final Multimap<Kon, Integer> lakarenAlderOchKonForSjukfalls = getLakarbefattningsForAllSjukfall(aisle, filter, range);
        List<SimpleKonDataRow> result = new ArrayList<>();
        for (Map.Entry<Integer, String> befattningCode : LAKARBEFATTNINGS.entrySet()) {
            final int femaleCount = Collections.frequency(lakarenAlderOchKonForSjukfalls.get(Kon.Female), befattningCode.getKey());
            final int maleCount = Collections.frequency(lakarenAlderOchKonForSjukfalls.get(Kon.Male), befattningCode.getKey());
            result.add(new SimpleKonDataRow(befattningCode.getValue(), femaleCount, maleCount));
        }
        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    private Multimap<Kon, Integer> getLakarbefattningsForAllSjukfall(Aisle aisle, Predicate<Fact> filter, Range range) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        final Multimap<Kon, Integer> sjukfallPerLakarbefattning = ArrayListMultimap.create();
        for (Sjukfall sjukfall : sjukfalls) {
            final Kon kon = sjukfall.getKon();
            for (se.inera.statistics.service.warehouse.Lakare lakare : sjukfall.getLakare()) {
                sjukfallPerLakarbefattning.put(kon, lakare.getBefattning());
            }
        }
        return sjukfallPerLakarbefattning;
    }

}
