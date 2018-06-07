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
package se.inera.statistics.service.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallIterator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is a common cache used to get better performance in ST.
 * It is a shared cache to make it easier to maintain the memory footprint.
 */
@Component
public class Cache {
    private static final Logger LOG = LoggerFactory.getLogger(Cache.class);
    private static final String AISLE = "AISLE";
    private static final String SJUKFALLGROUP = "SJUKFALLGROUP";
    private static final String VGENHET = "VGENHET";
    private static final String ENHET = "ENHET";
    private static final int DEFAULT_MAX_SIZE = 1000;

    @Value("${cache.maxsize:1000}")
    private String maxSize;

    private com.google.common.cache.Cache<String, Object> genericCache;

    private com.google.common.cache.Cache<String, Object> getGenericCache() {
        if (genericCache == null) {
            final Integer maxSizeInteger = ConversionHelper.parseInt(maxSize);
            final int maxSizeInt = maxSizeInteger != null ? maxSizeInteger : DEFAULT_MAX_SIZE;
            LOG.info("Creates cache with max size: {}", maxSizeInt);
            genericCache = CacheBuilder.newBuilder()
                    .softValues().expireAfterWrite(1, TimeUnit.DAYS).build();
        }
        return genericCache;
    }

    @Scheduled(cron = "${scheduler.factReloadJob.cron}")
    public void clearCaches() {
        getGenericCache().invalidateAll();
    }

    public List<SjukfallGroup> getSjukfallGroups(SjukfallGroupCacheKey key) {
        LOG.info("Getting sjukfallgroups: {}", key.getKey());
        try {
            return (List<SjukfallGroup>) getGenericCache().get(SJUKFALLGROUP + key.getKey(), () -> loadSjukfallgroups(key));
        } catch (ExecutionException e) {
            LOG.warn("Failed to get value from cache");
            LOG.debug("Failed to get value from cache", e);
            return loadSjukfallgroups(key);
        }
    }

    private List<SjukfallGroup> loadSjukfallgroups(SjukfallGroupCacheKey key) {
        LOG.info("Sjukfallgroups not cached: {}", key.getKey());
        final LocalDate from = key.getFrom();
        final int periods = key.getPeriods();
        final int periodSize = key.getPeriodSize();
        final Aisle aisle = key.getAisle();
        final FilterPredicates filter = key.getFilter();
        return Lists
                .newArrayList(new SjukfallIterator(from, periods, periodSize, aisle, filter));
    }

    public Aisle getAisle(HsaIdVardgivare vardgivarId, Function<HsaIdVardgivare, Aisle> loader) {
        LOG.info("Getting aisle: {}", vardgivarId);
        try {
            return (Aisle) getGenericCache().get(AISLE + vardgivarId.getId(), () -> aisleLoader(vardgivarId, loader));
        } catch (ExecutionException e) {
            LOG.warn("Failed to get aisle from cache for vg: " + vardgivarId);
            return aisleLoader(vardgivarId, loader);
        }
    }

    private Aisle aisleLoader(HsaIdVardgivare vardgivarId, Function<HsaIdVardgivare, Aisle> loader) {
        LOG.info("Aisle not cached: {}", vardgivarId);
        return loader.apply(vardgivarId);
    }

    private List<HsaIdEnhet> loadVgEnhets(HsaIdVardgivare vardgivareId, Function<HsaIdVardgivare, List<Enhet>> loader) {
        LOG.info("VgEnhets not cached: {}", vardgivareId);
        final List<Enhet> allEnhetsForVg = loader.apply(vardgivareId);
        for (Enhet enhet : allEnhetsForVg) {
            getGenericCache().put(ENHET + enhet.getEnhetId(), enhet);
        }
        return allEnhetsForVg.stream().map(Enhet::getEnhetId).collect(Collectors.toList());
    }

    public List<HsaIdEnhet> getVgEnhets(HsaIdVardgivare vardgivareId, Function<HsaIdVardgivare, List<Enhet>> loader) {
        LOG.info("Getting VgEnhets: {}", vardgivareId);
        try {
            return (List<HsaIdEnhet>) getGenericCache().get(VGENHET + vardgivareId, () -> loadVgEnhets(vardgivareId, loader));
        } catch (ExecutionException e) {
            LOG.warn("Could not get enhets from cache for vg: " + vardgivareId);
            return loadVgEnhets(vardgivareId, loader);
        }
    }

    public Collection<Enhet> getEnhetsWithHsaId(Collection<HsaIdEnhet> enhetIds,
                                                Function<Collection<HsaIdEnhet>, Collection<Enhet>> loader) {
        final List<String> cacheKeys = enhetIds.stream().map(hsaIdEnhet -> ENHET + hsaIdEnhet.getId()).collect(Collectors.toList());
        final ImmutableMap<String, Object> allPresent = getGenericCache().getAllPresent(cacheKeys);
        if (allPresent.size() == enhetIds.size()) {
            LOG.info("All enhets cached");
            return allPresent.values().stream().map(o -> (Enhet) o).collect(Collectors.toList());
        }
        final HashSet<HsaIdEnhet> hsaIdEnhetsNotInCache = new HashSet<>(enhetIds);
        hsaIdEnhetsNotInCache.removeAll(allPresent.keySet().stream().map(
                s -> new HsaIdEnhet(s.substring(ENHET.length()))).collect(Collectors.toList()));
        final Set<Enhet> enhets = new HashSet<>(loader.apply(hsaIdEnhetsNotInCache));
        for (Enhet enhet : enhets) {
            getGenericCache().put(ENHET + enhet.getEnhetId().getId(), enhet);
        }
        enhets.addAll(allPresent.values().stream().map(o -> (Enhet) o).collect(Collectors.toList()));
        LOG.info("All enhets not cached");
        return enhets;
    }

}
