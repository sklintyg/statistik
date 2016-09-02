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
package se.inera.statistics.service.warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.KonDataResponse;
import se.inera.statistics.service.report.model.KonDataRow;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.ReportUtil;
import se.inera.statistics.service.warehouse.query.CounterFunction;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;

@Component
public class SjukfallUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallUtil.class);

    private LoadingCache<SjukfallGroupCacheKey, List<SjukfallGroup>> sjukfallGroupsCache;

    public void clearSjukfallGroupCache() {
        getSjukfallGroupsCache().invalidateAll();
    }

    public static final SjukfallFilter ALL_ENHETER = new SjukfallFilter(fact -> true, sjukfall -> true, SjukfallFilter.HASH_EMPTY_FILTER);

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
                            final SjukfallFilter filter = key.getFilter();
                            final boolean useOriginalSjukfallStart = key.isUseOriginalSjukfallStart();
                            return Lists.newArrayList(new SjukfallIterator(from, periods, periodSize, aisle, filter, useOriginalSjukfallStart));
                        }
                    });
        }
        return sjukfallGroupsCache;
    }

    public SjukfallFilter createEnhetFilter(HsaIdEnhet... enhetIds) {
        final Set<Integer> availableEnhets = new HashSet<>(Lists.transform(Arrays.asList(enhetIds), enhetId -> Warehouse.getEnhet(enhetId)));
        final String hashValue = SjukfallFilter.getHashValueForEnhets(availableEnhets.toArray());
        return new SjukfallFilter(fact -> availableEnhets.contains(fact.getEnhet()), sjukfall -> true, hashValue);
    }

    public Iterable<SjukfallGroup> sjukfallGrupperUsingOriginalSjukfallStart(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final SjukfallFilter filter) {
        return sjukfallGrupper(from, periods, periodSize, aisle, filter, true);
    }

    public Iterable<SjukfallGroup> sjukfallGrupper(final LocalDate from, final int periods, final int periodSize, final Aisle aisle, final SjukfallFilter filter) {
        return sjukfallGrupper(from, periods, periodSize, aisle, filter, false);
    }

    List<SjukfallGroup> sjukfallGrupper(LocalDate from, int periods, int periodSize, Aisle aisle, SjukfallFilter sjukfallFilter, boolean useOriginalSjukfallStart) {
        try {
            return getSjukfallGroupsCache().get(new SjukfallGroupCacheKey(from, periods, periodSize, aisle, sjukfallFilter, useOriginalSjukfallStart));
        } catch (ExecutionException e) {
            //Failed to get from cache. Do nothing and fall through.
            LOG.warn("Failed to get value from cache");
            LOG.debug("Failed to get value from cache", e);
        }
        return Lists.newArrayList(new SjukfallIterator(from, periods, periodSize, aisle, sjukfallFilter, useOriginalSjukfallStart));
    }

    //CHECKSTYLE:OFF ParameterNumberCheck
    public <T> KonDataResponse calculateKonDataResponseUsingOriginalSjukfallStart(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, List<String> groupNames, List<T> groupIds, CounterFunction<T> counterFunction) {
        return calculateKonDataResponse(aisle, filter, start, periods, periodSize, groupNames, groupIds, counterFunction, true);
    }

    public <T> KonDataResponse calculateKonDataResponse(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, List<String> groupNames, List<T> groupIds, CounterFunction<T> counterFunction) {
        return calculateKonDataResponse(aisle, filter, start, periods, periodSize, groupNames, groupIds, counterFunction, false);
    }

    private <T> KonDataResponse calculateKonDataResponse(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, List<String> groupNames, List<T> groupIds, CounterFunction<T> counterFunction, boolean useOriginalSjukfallStart) {
        List<KonDataRow> rows = new ArrayList<>();
        for (SjukfallGroup sjukfallGroup : sjukfallGrupper(start, periods, periodSize, aisle, filter, useOriginalSjukfallStart)) {
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

    public <T> KonDataResponse calculateKonDataResponse(Aisle aisle, SjukfallFilter filter, LocalDate start, int periods, int periodSize, Function<Sjukfall, Collection<T>> groupsFunction, CounterFunction<T> counterFunction) {
        List<KonDataRow> rows = new ArrayList<>();
        final Iterable<SjukfallGroup> sjukfallGroups = sjukfallGrupper(start, periods, periodSize, aisle, filter);
        final HashSet<T> hashSet = new LinkedHashSet<>();
        for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                hashSet.addAll(groupsFunction.apply(sjukfall));
            }
        }
        for (SjukfallGroup sjukfallGroup : sjukfallGroups) {
            final HashMultiset<T> maleCounter = HashMultiset.create();
            final HashMultiset<T> femaleCounter = HashMultiset.create();
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                final HashMultiset<T> currentCounter = Kon.Female.equals(sjukfall.getKon()) ? femaleCounter : maleCounter;
                counterFunction.addCount(sjukfall, currentCounter);
            }
            List<KonField> list = new ArrayList<>();
            for (T id : hashSet) {
                list.add(new KonField(femaleCounter.count(id), maleCounter.count(id)));
            }
            rows.add(new KonDataRow(ReportUtil.toDiagramPeriod(sjukfallGroup.getRange().getFrom()), list));
        }

        final Collection<String> groupNames = Collections2.transform(hashSet, new Function<T, String>() {
            @Override
            public String apply(T integer) {
                return String.valueOf(integer);
            }
        });
        return new KonDataResponse(new ArrayList<>(groupNames), rows);
    }
    //CHECKSTYLE:ON

    public SimpleKonResponse<SimpleKonDataRow> calculateSimpleKonResponse(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, CounterFunction<Integer> toCount, List<Integer> groups) {
        return calculateSimpleKonResponse(toCount, groups, sjukfallGrupper(from, periods, periodLength, aisle, filter));
    }

    public SimpleKonResponse<SimpleKonDataRow> calculateSimpleKonResponseUsingOriginalSjukfallStart(Aisle aisle, SjukfallFilter filter, LocalDate from, int periods, int periodLength, CounterFunction<Integer> toCount, List<Integer> groups) {
        return calculateSimpleKonResponse(toCount, groups, sjukfallGrupperUsingOriginalSjukfallStart(from, periods, periodLength, aisle, filter));
    }

    private SimpleKonResponse<SimpleKonDataRow> calculateSimpleKonResponse(CounterFunction<Integer> toCount, List<Integer> groups, Iterable<SjukfallGroup> sjukfallGroups) {
        List<SimpleKonDataRow> rows = new ArrayList<>();
        HashMultiset<Integer> maleCounter = HashMultiset.create();
        HashMultiset<Integer> femaleCounter = HashMultiset.create();
        for (SjukfallGroup sjukfallGroup: sjukfallGroups) {
            for (Sjukfall sjukfall : sjukfallGroup.getSjukfall()) {
                HashMultiset<Integer> counter = Kon.Male.equals(sjukfall.getKon()) ? maleCounter : femaleCounter;
                toCount.addCount(sjukfall, counter);
            }
        }
        for (Integer group : groups) {
            rows.add(new SimpleKonDataRow(String.valueOf(group), femaleCounter.count(group), maleCounter.count(group)));
        }
        return new SimpleKonResponse<>(rows);
    }

}
