/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse.query;

import static se.inera.statistics.service.report.util.Ranges.range;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Lakare;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public final class LakaresAlderOchKonQuery {

    private static final Ranges RANGES_LAKARES_ALDER_OCH_KON = new Ranges(range("under 30 år", 30), range("30-39 år", 40),
        range("40-49 år", 50), range("50-59 år", 60), range("över 59 år", Integer.MAX_VALUE));
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
            case FEMALE:
                return "Kvinnlig läkare";
            case MALE:
                return "Manlig läkare";
            case UNKNOWN:
                return "Okänt kön";
        }

        throw new IllegalArgumentException("Unhandled type: " + kon);
    }

    public SimpleKonResponse getSjukfallPerLakaresAlderOchKon(Aisle aisle, FilterPredicates filter, LocalDate start,
        int periods, int periodLength) {
        final Function<Sjukfall, Collection<Lakare>> getLakaresFunc = Sjukfall::getLakare;
        final KonDataResponse konDataResponse = getSjukfallPerLakaresAlderOchKonCommon(aisle, filter, start, periods, periodLength,
            getLakaresFunc);
        return SimpleKonResponse.create(konDataResponse);
    }

    public KonDataResponse getSjukfallPerLakaresAlderOchKonSomTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
        int periodLength) {
        final Function<Sjukfall, Collection<Lakare>> getLakaresFunc = sjukfall1 -> Collections.singleton(sjukfall1.getLastLakare());
        return getSjukfallPerLakaresAlderOchKonCommon(aisle, filter, start, periods, periodLength, getLakaresFunc);
    }

    private KonDataResponse getSjukfallPerLakaresAlderOchKonCommon(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
        int periodLength, final Function<Sjukfall, Collection<Lakare>> getLakaresFunc) {
        final ArrayList<Ranges.Range> rangesList = Lists.newArrayList(RANGES_LAKARES_ALDER_OCH_KON);
        final CounterFunction<String> counterFunction = new CounterFunction<String>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<String> counter) {
                for (Lakare lakare : getLakaresFunc.apply(sjukfall)) {
                    final String rangeName = getRangeNameForAge(lakare.getAge());
                    counter.add(getLakareAlderOchKonTitle(lakare.getKon(), rangeName));
                }
            }

            private String getRangeNameForAge(int age) {
                if (age > 0) {
                    for (Ranges.Range range : rangesList) {
                        if (range.getCutoff() > age) {
                            return range.getName();
                        }
                    }
                }
                return UNKNOWN_AGE_NAME;
            }
        };
        final List<String> names = getRangeNames(rangesList);
        final KonDataResponse response = sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, names,
            counterFunction);
        final List<String> groupsThatShouldAlwaysBeRetained = getGroupsThatShouldAlwaysBeRetained(rangesList);
        return KonDataResponse.createNewWithoutEmptyGroups(response, groupsThatShouldAlwaysBeRetained);
    }

    private List<String> getRangeNames(ArrayList<Ranges.Range> ranges) {
        final List<String> names = new ArrayList<>();
        for (Ranges.Range range : ranges) {
            for (Kon kon : Kon.values()) {
                names.add(getLakareAlderOchKonTitle(kon, range.getName()));
            }
        }
        names.add(getLakareAlderOchKonTitle(Kon.UNKNOWN, UNKNOWN_AGE_NAME));
        return names;
    }

    private List<String> getGroupsThatShouldAlwaysBeRetained(ArrayList<Ranges.Range> ranges) {
        final List<String> groupsThatShouldAlwaysBeRetained = new ArrayList<>();
        for (Ranges.Range range : ranges) {
            groupsThatShouldAlwaysBeRetained.add(getLakareAlderOchKonTitle(Kon.MALE, range.getName()));
            groupsThatShouldAlwaysBeRetained.add(getLakareAlderOchKonTitle(Kon.FEMALE, range.getName()));
        }
        return groupsThatShouldAlwaysBeRetained;
    }

}
