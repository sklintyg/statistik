package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static se.inera.statistics.service.report.util.Ranges.range;

public final class LakaresAlderOchKonQuery {

    private static final Ranges RANGES_LAKARES_ALDER_OCH_KON = new Ranges(range("under 30 år", 30), range("30-39 år", 40), range("40-49 år", 50), range("50-59 år", 60), range("över 59 år", Integer.MAX_VALUE));
    private static final String UNKNOWN_AGE_RANGE = "UnknownAgeRange";

    private LakaresAlderOchKonQuery() {
    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakaresAlderOchKon(Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        return new LakaresAlderOchKonQuery().getSjukfallPerLakaresAlderOchKonResponse(aisle, filter, range, perioder, periodlangd);
    }

    private SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakaresAlderOchKonResponse(Aisle aisle, Predicate<Fact> filter, Range range, int perioder, int periodlangd) {
        final Multimap<Kon, String> lakarenAlderOchKonForSjukfalls = getLakarensAlderOchKonForAllSjukfall(aisle, filter, range);
        List<SimpleKonDataRow> result = new ArrayList<>();
        for (Ranges.Range ageRange : RANGES_LAKARES_ALDER_OCH_KON) {
            final String ageRangeKey = String.valueOf(ageRange.getCutoff());
            final String ageRangeTitle = ageRange.getName();
            result.addAll(getDataForAgeRange(lakarenAlderOchKonForSjukfalls, ageRangeKey, ageRangeTitle));
        }

        final List<SimpleKonDataRow> dataForUnknownAge = getDataForAgeRange(lakarenAlderOchKonForSjukfalls, UNKNOWN_AGE_RANGE, "okänd ålder");
        for (SimpleKonDataRow simpleKonDataRow : dataForUnknownAge) {
            if ((simpleKonDataRow.getFemale() + simpleKonDataRow.getMale()) > 0) {
                result.add(simpleKonDataRow);
            }
        }

        return new SimpleKonResponse<>(result, perioder * periodlangd);
    }

    private List<SimpleKonDataRow> getDataForAgeRange(Multimap<Kon, String> lakarenAlderOchKonForSjukfalls, String ageRangeKey, String ageRangeTitle) {
        List<SimpleKonDataRow> result = new ArrayList<>();
        for (Kon kon : Kon.values()) {
            final int femaleCount = Collections.frequency(lakarenAlderOchKonForSjukfalls.get(Kon.Female), ageRangeKey + kon);
            final int maleCount = Collections.frequency(lakarenAlderOchKonForSjukfalls.get(Kon.Male), ageRangeKey + kon);
            if (!Kon.Unknown.equals(kon) || femaleCount + maleCount > 0) {
                result.add(new SimpleKonDataRow(getLakareAlderOchKonTitle(kon, ageRangeTitle), femaleCount, maleCount));
            }
        }
        return result;
    }

    private String getLakareAlderOchKonTitle(Kon kon, String ageRangeTitle) {
        return getLakareAlderOchKonTitleKonPart(kon) + " " + ageRangeTitle;
    }

    private String getLakareAlderOchKonTitleKonPart(Kon kon) {
        switch (kon) {
            case Female: return "Kvinnlig läkare";
            case Male: return "Manlig läkare";
            case Unknown: return "Okänt kön";
            default: throw new IllegalArgumentException("Unhandled type: " + kon);
        }
    }

    private Multimap<Kon, String> getLakarensAlderOchKonForAllSjukfall(Aisle aisle, Predicate<Fact> filter, Range range) {
        Collection<Sjukfall> sjukfalls = SjukfallUtil.active(range, aisle, filter);
        final Multimap<Kon, String> sjukfallPerLakare = ArrayListMultimap.create();
        for (Sjukfall sjukfall : sjukfalls) {
            for (se.inera.statistics.service.warehouse.Lakare lakare : sjukfall.getLakare()) {
                sjukfallPerLakare.put(sjukfall.getKon(), getAgeRangeString(lakare.getAge()) + lakare.getKon());
            }
        }
        return sjukfallPerLakare;
    }

    private String getAgeRangeString(int age) {
        if (age > 0) {
            final Ranges.Range ageRange = RANGES_LAKARES_ALDER_OCH_KON.rangeFor(age);
            return String.valueOf(ageRange.getCutoff());
        }
        return UNKNOWN_AGE_RANGE;
    }

}
