/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Lakare;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;

public final class LakarbefattningQuery {

    public static final Integer NO_BEFATTNING_CODE = -1;
    private static final String NO_BEFATTNING_TEXT = "Ej läkarbefattning";
    public static final Integer UNKNOWN_BEFATTNING_CODE = -2;
    private static final String UNKNOWN_BEFATTNING_TEXT = "Okänd befattning";

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

    private LakarbefattningQuery() {
    }

     public static SimpleKonResponse<SimpleKonDataRow> getSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final KonDataResponse sjukfallSomTidsserie = getSjukfallSomTidsserie(aisle, filter, start, periods, periodLength, sjukfallUtil);
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

    public static KonDataResponse getSjukfallSomTidsserie(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final ArrayList<Map.Entry<Integer, String>> ranges = new ArrayList<>(getAllLakarbefattnings(true).entrySet());
        final List<String> names = Lists.transform(ranges, new Function<Map.Entry<Integer, String>, String>() {
            @Override
            public String apply(Map.Entry<Integer, String> entry) {
                return entry.getValue();
            }
        });
        final List<Integer> ids = Lists.transform(ranges, new Function<Map.Entry<Integer, String>, Integer>() {
            @Override
            public Integer apply(Map.Entry<Integer, String> entry) {
                return entry.getKey();
            }
        });
        final CounterFunction<Integer> counterFunction = new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                for (Lakare lakare : sjukfall.getLakare()) {
                    final List<Integer> lakarbefattnings = getLakarbefattnings(lakare);
                    for (Integer befattningId : lakarbefattnings) {
                        counter.add(befattningId);
                    }
                    if (lakarbefattnings.isEmpty()) {
                        counter.add(getCorrectCodeWhenNoLakarbefattningExists(lakare.getBefattnings()));
                    }
                }
            }
        };
        final KonDataResponse response = sjukfallUtil.calculateKonDataResponse(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
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
