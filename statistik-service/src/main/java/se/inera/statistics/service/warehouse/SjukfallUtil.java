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
package se.inera.statistics.service.warehouse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.CounterFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class SjukfallUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallUtil.class);

    private LoadingCache<SjukfallGroupCacheKey, List<SjukfallGroup>> sjukfallGroupsCache;

    public void clearSjukfallGroupCache() {
        getSjukfallGroupsCache().invalidateAll();
    }

    public static final SjukfallFilter ALL_ENHETER = new SjukfallFilter(new Predicate<Fact>() {
        @Override
        public boolean apply(Fact fact) {
            return true;
        }
    }, SjukfallFilter.HASH_EMPTY_FILTER);

    public static final int LONG_LIMIT = 90;

    public SjukfallUtil() {
    }

    private LoadingCache<SjukfallGroupCacheKey, List<SjukfallGroup>> getSjukfallGroupsCache() {
        if (sjukfallGroupsCache == null) {
            sjukfallGroupsCache = CacheBuilder.newBuilder()
                    .softValues()
                    .build(new CacheLoader<SjukfallGroupCacheKey, List<SjukfallGroup>>() {
                        public List<SjukfallGroup> load(SjukfallGroupCacheKey key) {
                            final LocalDate from = key.getFrom();
                            final int periods = key.getPeriods();
                            final int periodSize = key.getPeriodSize();
                            final Aisle aisle = key.getAisle();
                            final Predicate<Fact> filter = key.getFilter();
                            final boolean useOriginalSjukfallStart = key.isUseOriginalSjukfallStart();
                            return Lists.newArrayList(new SjukfallIterator(from, periods, periodSize, aisle, filter, useOriginalSjukfallStart));
                        }
                    });
        }
        return sjukfallGroupsCache;
    }

    public Collection<Sjukfall> active(Range range, Aisle aisle, Predicate<Fact> filter) {
        return active(calculateSjukfall(aisle, filter, WidelineConverter.toDay(firstDayAfter(range))), range);
    }

    private Collection<Sjukfall> active(Collection<Sjukfall> all, Range range) {
        int start = WidelineConverter.toDay(range.getFrom());
        int end = WidelineConverter.toDay(firstDayAfter(range));
        Collection<Sjukfall> active = new ArrayList<>();
        for (Sjukfall sjukfall : all) {
            if (sjukfall.in(start, end)) {
                active.add(sjukfall);
            }
        }
        return active;
    }

    private Collection<Sjukfall> calculateSjukfall(Aisle aisle, Predicate<Fact> filter, int cutoff) {
        Collection<Sjukfall> sjukfalls = new ArrayList<>();
        Map<Long, Sjukfall> active = new HashMap<>();
        for (Fact line : aisle) {
            if (line.getStartdatum() >= cutoff) {
                break;
            }
            long key = line.getPatient();
            Sjukfall sjukfall = active.get(key);

            if (sjukfall == null) {
                if (filter.apply(line)) {
                    sjukfall = new Sjukfall(line);
                    active.put(key, sjukfall);
                }
            } else {
                Sjukfall nextSjukfall = sjukfall.join(line);
                active.put(key, nextSjukfall);
                if (!nextSjukfall.isExtended()) {
                    sjukfalls.add(sjukfall);
                }
            }
        }
        for (Sjukfall sjukfall : active.values()) {
            sjukfalls.add(sjukfall);
        }
        return sjukfalls;
    }

    @VisibleForTesting
    public Collection<Sjukfall> calculateSjukfall(Aisle aisle) {
        return calculateSjukfall(aisle, ALL_ENHETER.getFilter(), Integer.MAX_VALUE);
    }

    @VisibleForTesting
    Collection<Sjukfall> calculateSjukfall(Aisle aisle, Integer... enhetIds) {
        return calculateSjukfall(aisle, createEnhetFilterFromInternalIntValues(enhetIds).getFilter(), Integer.MAX_VALUE);
    }

    public SjukfallFilter createEnhetFilter(String... enhetIds) {
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(Arrays.asList(enhetIds), new Function<String, Integer>() {
            @Override
            public Integer apply(String enhetId) {
                return Warehouse.getEnhet(enhetId);
            }
        }));
        return new SjukfallFilter(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return availableEnhets.contains(fact.getEnhet());
            }
        }, SjukfallFilter.getHashValueForEnhets(availableEnhets.toArray()));
    }

    public SjukfallFilter createEnhetFilterFromInternalIntValues(Integer... enhetIds) {
        final HashSet<Integer> availableEnhets = new HashSet<>(Arrays.asList(enhetIds));
        return new SjukfallFilter(new Predicate<Fact>() {
            @Override
            public boolean apply(Fact fact) {
                return availableEnhets.contains(fact.getEnhet());
            }
        }, SjukfallFilter.getHashValueForEnhets(availableEnhets.toArray()));
    }

    public Iterable<SjukfallGroup> sjukfallGrupperUsingOriginalSjukfallStart(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final SjukfallFilter filter) {
        return sjukfallGrupper(from, periods, periodSize, aisle, filter, true);
    }

    public Iterable<SjukfallGroup> sjukfallGrupper(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final SjukfallFilter filter) {
        return sjukfallGrupper(from, periods, periodSize, aisle, filter, false);
    }

    private List<SjukfallGroup> sjukfallGrupper(LocalDate from, int periods, int periodSize, Aisle aisle, SjukfallFilter sjukfallFilter, boolean useOriginalSjukfallStart) {
        if (SjukfallFilter.HASH_EMPTY_FILTER.equals(sjukfallFilter.getHash())) {
            try {
                return getSjukfallGroupsCache().get(new SjukfallGroupCacheKey(from, periods, periodSize, aisle, sjukfallFilter, useOriginalSjukfallStart));
            } catch (ExecutionException e) {
                //Failed to get from cache. Do nothing and fall through.
                LOG.warn("Failed to get value from cache");
            }
        }
        return Lists.newArrayList(new SjukfallIterator(from, periods, periodSize, aisle, sjukfallFilter.getFilter(), useOriginalSjukfallStart));
    }

    static LocalDate firstDayAfter(Range range) {
        return range.getTo().plusMonths(1);
    }

    public int getLong(Collection<Sjukfall> sjukfalls) {
        int count = 0;
        for (Sjukfall sjukfall : sjukfalls) {
            if (sjukfall.getRealDays() > LONG_LIMIT) {
                count++;
            }
        }
        return count;
    }

    //CHECKSTYLE:OFF ParameterNumberCheck
    public <T> KonDataResponse calculateKonDataResponse(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, List<String> groupNames, List<T> groupIds, CounterFunction<T> counterFunction) {
        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallGrupper(start, periods, periodSize, aisle, filter)) {
            final HashMultiset<T> maleCounter = HashMultiset.create();
            final HashMultiset<T> femaleCounter = HashMultiset.create();
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                final HashMultiset<T> currentCounter = Kon.Female.equals(sjukfall.getKon()) ? femaleCounter : maleCounter;
                counterFunction.addCount(sjukfall, currentCounter);
            }
            List<KonField> list = new ArrayList<>(groupIds.size());
            for (T id : groupIds) {
                list.add(new KonField(femaleCounter.count(id), maleCounter.count(id)));
            }
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }

        return new KonDataResponse(groupNames, rows);
    }
    //CHECKSTYLE:ON

    public SimpleKonResponse<SimpleKonDataRow> calculateSimpleKonResponse(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, Function<Sjukfall, Integer> toCount, List<Integer> groups) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        HashMultiset<Integer> maleCounter = HashMultiset.create();
        HashMultiset<Integer> femaleCounter = HashMultiset.create();
        for (SjukfallGroup sjukfallGroup: sjukfallGrupper(from, periods, periodLength, aisle, filter)) {
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                HashMultiset<Integer> counter = Kon.Male.equals(sjukfall.getKon()) ? maleCounter : femaleCounter;
                counter.add(toCount.apply(sjukfall));
            }
        }
        for (Integer group : groups) {
            rows.add(new SimpleKonDataRow(String.valueOf(group), femaleCounter.count(group), maleCounter.count(group)));
        }
        return new SimpleKonResponse<>(rows, periodLength);
    }

}
