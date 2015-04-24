/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse.query;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Lakare;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static se.inera.statistics.service.report.util.Ranges.range;

public final class LakaresAlderOchKonQuery {

    private static final Ranges RANGES_LAKARES_ALDER_OCH_KON = new Ranges(range("under 30 år", 30), range("30-39 år", 40), range("40-49 år", 50), range("50-59 år", 60), range("över 59 år", Integer.MAX_VALUE));
    public static final String UNKNOWN_AGE_NAME = "okänd ålder";
    private final SjukfallUtil sjukfallUtil;

    public LakaresAlderOchKonQuery(SjukfallUtil sjukfallUtil) {
        this.sjukfallUtil = sjukfallUtil;
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

    public SimpleKonResponse<SimpleKonDataRow> getSjukfallPerLakaresAlderOchKon(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength) {
        final KonDataResponse konDataResponse = getSjukfallPerLakaresAlderOchKonSomTidsserie(aisle, filter, start, periods, periodLength);
        return SimpleKonResponse.create(konDataResponse);
    }

    public KonDataResponse getSjukfallPerLakaresAlderOchKonSomTidsserie(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength) {
        final ArrayList<Ranges.Range> ranges = Lists.newArrayList(RANGES_LAKARES_ALDER_OCH_KON);
        final List<String> names = new ArrayList<>();
        for (Ranges.Range range : ranges) {
            for (Kon kon : Kon.values()) {
                names.add(getLakareAlderOchKonTitle(kon, range.getName()));
            }
        }
        names.add(getLakareAlderOchKonTitle(Kon.Unknown, UNKNOWN_AGE_NAME));
        Lists.transform(ranges, new Function<Ranges.Range, String>() {
            @Override
            public String apply(Ranges.Range range) {
                for (Kon kon : Kon.values()) {
                    getLakareAlderOchKonTitle(kon, range.getName());
                }
                return range.getName();
            }
        });
        final CounterFunction<String> counterFunction = new CounterFunction<String>() {
            @Override
            public void addCount(CounterFunctionInput input, HashMultiset<String> counter) {
                final Set<Lakare> lakares = input.getSjukfall().getLakare();
                for (Lakare lakare : lakares) {
                    final String rangeName = getRangeNameForAge(lakare.getAge());
                    counter.add(getLakareAlderOchKonTitle(lakare.getKon(), rangeName));
                }
            }

            private String getRangeNameForAge(int age) {
                if (age > 0) {
                    for (Ranges.Range range: ranges) {
                        if (range.getCutoff() > age) {
                            return range.getName();
                        }
                    }
                }
                return UNKNOWN_AGE_NAME;
            }
        };

        final KonDataResponse response = sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, names, counterFunction);
        final List<String> groupsThatShouldAlwaysBeRetained = new ArrayList<>();
        for (Ranges.Range range : ranges) {
            groupsThatShouldAlwaysBeRetained.add(getLakareAlderOchKonTitle(Kon.Male, range.getName()));
            groupsThatShouldAlwaysBeRetained.add(getLakareAlderOchKonTitle(Kon.Female, range.getName()));
        }
        return KonDataResponse.createNewWithoutEmptyGroups(response, groupsThatShouldAlwaysBeRetained);
    }

}
