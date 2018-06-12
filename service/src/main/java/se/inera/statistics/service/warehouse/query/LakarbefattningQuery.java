/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.Lakare;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;

public final class LakarbefattningQuery {

    public static final Integer NO_BEFATTNING_CODE = -1;
    private static final String NO_BEFATTNING_TEXT = "Ej läkarbefattning";
    public static final Integer UNKNOWN_BEFATTNING_CODE = -2;
    private static final String UNKNOWN_BEFATTNING_TEXT = "Okänd befattning";

    private LakarbefattningQuery() {
    }

    private static Map<Integer, String> getAllLakarbefattnings(boolean includeInternalBefattnings) {
        Map<Integer, String> lakarbefattnings = new LinkedHashMap<>();
        // CHECKSTYLE:OFF MagicNumber
        lakarbefattnings.put(201010, "Överläkare");
        lakarbefattnings.put(201011, "Distriktsläkare/Specialist allmänmedicin");
        lakarbefattnings.put(201012, "Skolläkare");
        lakarbefattnings.put(201013, "Företagsläkare");
        lakarbefattnings.put(202010, "Specialistläkare");
        lakarbefattnings.put(203010, "Läkare legitimerad, specialiseringstjänstgöring");
        lakarbefattnings.put(203090, "Läkare legitimerad, annan");
        lakarbefattnings.put(204010, "Läkare ej legitimerad, allmäntjänstgöring");
        lakarbefattnings.put(204090, "Läkare ej legitimerad, annan");
        if (includeInternalBefattnings) {
            lakarbefattnings.put(NO_BEFATTNING_CODE, NO_BEFATTNING_TEXT);
            lakarbefattnings.put(UNKNOWN_BEFATTNING_CODE, UNKNOWN_BEFATTNING_TEXT);
        }
        return lakarbefattnings;
        // CHECKSTYLE:ON MagicNumber
    }

    public static SimpleKonResponse getSjukfall(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
            int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<Sjukfall, Collection<Lakare>> getLakare = Sjukfall::getLakare;
        final KonDataResponse sjukfallSomTidsserie = getSjukfallCommon(aisle, filter, start, periods, periodLength, sjukfallUtil,
                getLakare);
        return SimpleKonResponse.create(sjukfallSomTidsserie);
    }

    private static List<Integer> getLakarbefattnings(Lakare lakare) {
        final List<Integer> lakarbefattnings = new ArrayList<>();
        final int[] allBefattnings = lakare.getBefattnings();
        final Set<Integer> existingLakarebefattnings = getAllLakarbefattnings(true).keySet();
        for (int befattning : allBefattnings) {
            if (existingLakarebefattnings.contains(befattning)) {
                lakarbefattnings.add(befattning);
            }
        }
        return lakarbefattnings;
    }

    public static KonDataResponse getSjukfallSomTidsserie(Aisle aisle, FilterPredicates filter, LocalDate start, int periods,
            int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<Sjukfall, Collection<Lakare>> getLakare = sjukfall -> Collections.singleton(sjukfall.getLastLakare());
        return getSjukfallCommon(aisle, filter, start, periods, periodLength, sjukfallUtil, getLakare);
    }

    private static KonDataResponse getSjukfallCommon(Aisle aisle, FilterPredicates filter, LocalDate start, int periods, int periodLength,
            SjukfallUtil sjukfallUtil, final Function<Sjukfall, Collection<Lakare>> getLakare) {
        final ArrayList<Map.Entry<Integer, String>> ranges = new ArrayList<>(getAllLakarbefattnings(true).entrySet());
        final List<String> names = ranges.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        final List<Integer> ids = ranges.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        final CounterFunction<Integer> counterFunction = (sjukfall, counter) -> {
            for (Lakare lakare : getLakare.apply(sjukfall)) {
                final List<Integer> lakarbefattnings = getLakarbefattnings(lakare);
                counter.addAll(lakarbefattnings);
                if (lakarbefattnings.isEmpty()) {
                    counter.add(getCorrectCodeWhenNoLakarbefattningExists(lakare.getBefattnings()));
                }
            }
        };
        final KonDataResponse response = sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids,
                counterFunction);
        return KonDataResponse.createNewWithoutEmptyGroups(response);
    }

    private static Integer getCorrectCodeWhenNoLakarbefattningExists(int[] befattnings) {
        for (int befattning : befattnings) {
            if (befattning < 0) {
                return befattning;
            }
        }
        if (befattnings.length == 0) {
            return UNKNOWN_BEFATTNING_CODE;
        }
        return NO_BEFATTNING_CODE;
    }

}
