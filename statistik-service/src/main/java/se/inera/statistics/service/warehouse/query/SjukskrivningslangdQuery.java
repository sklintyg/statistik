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

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.OverviewChartRow;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.report.util.SjukfallslangdUtil;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallFilter;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class SjukskrivningslangdQuery {
    private static Ranges ranges = SjukfallslangdUtil.RANGES;
    private static final int LONG_SJUKFALL = 90;
    public static final List<String> ENKLA_SJUKFALL_LABELS = Collections.unmodifiableList(Arrays.asList("Övriga", "Enkla upp till 60 dagar", "Enkla över 60 dagar"));
    public static final int GROUP_OVRIGA = 1;
    public static final int GROUP_SIMPLE_SHORT = 2;
    public static final int GROUP_SIMPLE_LONG = 3;
    private static final List<Integer> ENKLA_SJUKFALL_IDS = Collections.unmodifiableList(Arrays.asList(GROUP_OVRIGA, GROUP_SIMPLE_SHORT, GROUP_SIMPLE_LONG));

    private SjukskrivningslangdQuery() {
    }

    public static List<OverviewChartRow> getOverviewSjukskrivningslangd(Collection<Sjukfall> currentSjukfall, int noOfRows) {
        List<Counter<Ranges.Range>> toKeep = count(currentSjukfall, noOfRows);

        List<OverviewChartRow> result = new ArrayList<>();

        for (Counter<Ranges.Range> counter : toKeep) {
            int current = counter.getCount();
            result.add(new OverviewChartRow(counter.getKey().getName(), current));
        }

        return result;
    }

    private static Collection<Ranges.Range> rowsToKeep(Map<Ranges.Range, Counter<Ranges.Range>> count, int noOfRows) {
        List<Counter<Ranges.Range>> sorted = new ArrayList<>();
        for (Counter counter : count.values()) {
            sorted.add(counter);
        }
        Collections.sort(sorted);

        Collection<Ranges.Range> result = new HashSet<>();
        for (Counter<Ranges.Range> counter : sorted) {
            result.add(counter.getKey());
            if (result.size() == noOfRows) {
                break;
            }
        }

        return result;
    }

    public static List<Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls, int noOfRows) {
        Map<Ranges.Range, Counter<Ranges.Range>> map = count(sjukfalls);
        List<Counter<Ranges.Range>> result = new ArrayList<>();

        Collection<Ranges.Range> rowsToKeep = rowsToKeep(map, noOfRows);
        for (Ranges.Range range : ranges) {
            if (rowsToKeep.contains(range)) {
                result.add(map.get(range));
            }
        }

        return result;
    }

    public static Map<Ranges.Range, Counter<Ranges.Range>> count(Collection<Sjukfall> sjukfalls) {
        Map<Ranges.Range, Counter<Ranges.Range>> counters = Counter.mapFor(SjukfallslangdUtil.RANGES);
        for (Sjukfall sjukfall : sjukfalls) {
            Counter counter = counters.get(ranges.rangeFor(sjukfall.getRealDays()));
            counter.increase(sjukfall);
        }
        return counters;
    }

    public static KonDataResponse getEnklaSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, SjukfallUtil sjukfallUtil) {
        return sjukfallUtil.calculateKonDataResponseUsingOriginalSjukfallStart(aisle, filter, start, periods, periodSize, ENKLA_SJUKFALL_LABELS, ENKLA_SJUKFALL_IDS, new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                counter.add(getEnkelSjukfallGroup(sjukfall));

            }
        });
    }

    public static SimpleKonResponse<SimpleKonDataRow> getEnklaSjukfallTvarsnitt(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<Sjukfall, Integer> toCount = new Function<Sjukfall, Integer>() {
            @Override
            public Integer apply(Sjukfall sjukfall) {
                return getEnkelSjukfallGroup(sjukfall);
            }
        };
        SimpleKonResponse<SimpleKonDataRow> response = sjukfallUtil.calculateSimpleKonResponseUsingOriginalSjukfallStart(aisle, filter, from, periods, periodLength, toCount, ENKLA_SJUKFALL_IDS);
        ArrayList<SimpleKonDataRow> rowsWithCorrectNames = new ArrayList<>();
        for (SimpleKonDataRow row : response.getRows()) {
            rowsWithCorrectNames.add(new SimpleKonDataRow(getEnklaSjukfallGroupNameFromId(row.getName()), row.getData()));
        }
        return new SimpleKonResponse<>(rowsWithCorrectNames);
    }

    private static String getEnklaSjukfallGroupNameFromId(String id) {
        switch (Integer.parseInt(id)) {
            case GROUP_OVRIGA: return ENKLA_SJUKFALL_LABELS.get(0);
            case GROUP_SIMPLE_SHORT: return ENKLA_SJUKFALL_LABELS.get(1);
            case GROUP_SIMPLE_LONG: return ENKLA_SJUKFALL_LABELS.get(2);
            default: return "Okänd grupp";
        }
    }

    private static Integer getEnkelSjukfallGroup(Sjukfall sjukfall) {
        if (!sjukfall.isEnkelt()) {
            return GROUP_OVRIGA;
        }
        final int longSjukfallDays = 60;
        if (sjukfall.getRealDays() <= longSjukfallDays) {
            return GROUP_SIMPLE_SHORT;
        }
        return GROUP_SIMPLE_LONG;
    }

    public static SimpleKonResponse<SimpleKonDataRow> getLangaSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<SjukfallGroup, String> rowNameFunction = new Function<SjukfallGroup, String>() {
            @Override
            public String apply(SjukfallGroup sjukfallGroup) {
                return ReportUtil.toPeriod(sjukfallGroup.getRange().getFrom());
            }
        };
        return getLangaSjukfall(aisle, filter, from, periods, periodLength, sjukfallUtil, rowNameFunction);
    }

    public static SimpleKonResponse<SimpleKonDataRow> getLangaSjukfallTvarsnitt(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final Function<SjukfallGroup, String> rowNameFunction = new Function<SjukfallGroup, String>() {
            @Override
            public String apply(SjukfallGroup sjukfallGroup) {
                return "Mer än 90 dagar";
            }
        };
        return getLangaSjukfall(aisle, filter, from, periods, periodLength, sjukfallUtil, rowNameFunction);
    }

    private static SimpleKonResponse<SimpleKonDataRow> getLangaSjukfall(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil, Function<SjukfallGroup, String> rowNameFunction) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: sjukfallUtil.sjukfallGrupperUsingOriginalSjukfallStart(from, periods, periodLength, aisle, filter)) {
            Counter counter = new Counter<>("");
            for (Sjukfall sjukfall: sjukfallGroup.getSjukfall()) {
                if (sjukfall.getRealDays() > LONG_SJUKFALL) {
                    counter.increase(sjukfall);
                }
            }
            rows.add(new SimpleKonDataRow(rowNameFunction.apply(sjukfallGroup), counter.getCountFemale(), counter.getCountMale()));
        }

        return new SimpleKonResponse<>(rows);
    }

    public static SimpleKonResponse<SimpleKonDataRow> getSjuksrivningslangd(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup: sjukfallUtil.sjukfallGrupperUsingOriginalSjukfallStart(from, periods, periodLength, aisle, filter)) {
            Map<Ranges.Range, Counter<Ranges.Range>> counterMap = SjukskrivningslangdQuery.count(sjukfallGroup.getSjukfall());
            for (Ranges.Range i : SjukfallslangdUtil.RANGES) {
                Counter<Ranges.Range> counter = counterMap.get(i);
                rows.add(new SimpleKonDataRow(i.getName(), counter.getCountFemale(), counter.getCountMale()));
            }
        }
        return new SimpleKonResponse<>(rows);

    }

    public static KonDataResponse getSjuksrivningslangdomTidsserie(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodLength, SjukfallUtil sjukfallUtil) {
        final ArrayList<Ranges.Range> ranges = Lists.newArrayList(SjukfallslangdUtil.RANGES);
        final List<String> names = Lists.transform(ranges, new Function<Ranges.Range, String>() {
            @Override
            public String apply(Ranges.Range range) {
                return range.getName();
            }
        });
        final List<Integer> ids = Lists.transform(ranges, new Function<Ranges.Range, Integer>() {
            @Override
            public Integer apply(Ranges.Range range) {
                return range.getCutoff();
            }
        });
        final CounterFunction<Integer> counterFunction = new CounterFunction<Integer>() {
            @Override
            public void addCount(Sjukfall sjukfall, HashMultiset<Integer> counter) {
                final int length = sjukfall.getRealDays();
                final int rangeId = getRangeIdForLength(length);
                counter.add(rangeId);
            }

            private int getRangeIdForLength(int length) {
                for (Ranges.Range range: ranges) {
                    final int cutoff = range.getCutoff();
                    if (cutoff > length) {
                        return cutoff;
                    }
                }
                throw new IllegalStateException("Ranges have not been defined correctly. No range includes " + length);
            }

        };

        return sjukfallUtil.calculateKonDataResponseUsingOriginalSjukfallStart(aisle, filter, start, periods, periodLength, names, ids, counterFunction);
    }

    static int getLong(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getRealDays() > LONG_SJUKFALL) {
                count++;
            }
        }
        return count;
    }

}
