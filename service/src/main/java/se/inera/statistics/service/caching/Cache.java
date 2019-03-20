/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.javacrumbs.shedlock.core.SchedulerLock;
import se.inera.intyg.infra.monitoring.logging.LogMDCHelper;
import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;
import se.inera.statistics.service.warehouse.Aisle;
import se.inera.statistics.service.warehouse.FilterPredicates;
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.SjukfallGroup;
import se.inera.statistics.service.warehouse.SjukfallIterator;

/**
 * This is a common cache used to get better performance in ST.
 * It is a shared cache to make it easier to maintain the memory footprint.
 */
@Component
public class Cache {
    private static final Logger LOG = LoggerFactory.getLogger(Cache.class);
    private static final String REDIS_KEY_PREFIX = "INTYGSSTATISTIK_";
    private static final String AISLE = REDIS_KEY_PREFIX + "AISLE_";
    private static final String NATIONAL_DATA = REDIS_KEY_PREFIX + "NATIONAL_DATA_";
    private static final String SJUKFALLGROUP = REDIS_KEY_PREFIX + "SJUKFALLGROUP_";
    private static final String VGENHET = REDIS_KEY_PREFIX + "VGENHET_";
    private static final String ENHET = REDIS_KEY_PREFIX + "ENHET_";
    private static final String EXISTING_INTYGTYPES = REDIS_KEY_PREFIX + "EXISTING_INTYGTYPES_";
    private static final String JOB_NAME = "Cache.clearCaches";
    private static final int MILLIS_PER_SEC = 1000;
    private static final int SECS_PER_MIN = 60;
    private static final int MIN_PER_HOUR = 60;
    private static final long ONE_HOUR_IN_MILLIS = MIN_PER_HOUR * SECS_PER_MIN * MILLIS_PER_SEC;

    @Value("${cache.maxsize:1000}")
    private String maxSize;

    @Autowired
    @Qualifier("rediscache")
    private RedisTemplate<Object, Object> template;

    @Autowired
    private LogMDCHelper logMDCHelper;

    public Cache() {
    }

    /**
     * Used by tests.
     */
    public Cache(RedisTemplate<Object, Object> template, String maxSize) {
        this.template = template;
        this.maxSize = maxSize;
    }

    @Scheduled(cron = "${scheduler.factReloadJob.cron}")
    @SchedulerLock(name = JOB_NAME)
    public void clearCaches() {
        logMDCHelper.run(() -> {
            LOG.info("Clear Redis Cache Keys");
            template.delete(Lists.newArrayList(AISLE, SJUKFALLGROUP, VGENHET, ENHET, NATIONAL_DATA, EXISTING_INTYGTYPES));
        });
    }

    public List<SjukfallGroup> getSjukfallGroups(SjukfallGroupCacheKey key) {
        LOG.info("Getting sjukfallgroups: {}", key.getKey());
        return lookup(SJUKFALLGROUP, key.getKey(), () ->  loadSjukfallgroups(key));
    }

    public List<HsaIdEnhet> getVgEnhets(HsaIdVardgivare vardgivareId, Function<HsaIdVardgivare, List<Enhet>> loader) {
        LOG.info("Getting VgEnhets: {}", vardgivareId);
        return lookup(VGENHET, vardgivareId.getId(), () -> loadVgEnhets(vardgivareId, loader));
    }

    public Aisle getAisle(HsaIdVardgivare vardgivarId, Function<HsaIdVardgivare, Aisle> loader) {
        LOG.info("Getting aisle: {}", vardgivarId);
        return lookup(AISLE, vardgivarId.getId(), () -> aisleLoader(vardgivarId, loader));
    }

    public Collection<Enhet> getEnhetsWithHsaId(final Collection<HsaIdEnhet> enhetIds,
                                                final Function<Collection<HsaIdEnhet>, Collection<Enhet>> loader) {
        final HashOperations<Object, Object, Object> hashOps = template.opsForHash();
        final List<Enhet> cachedEnhets = hashOps.multiGet(ENHET, enhetIds.stream().map(HsaIdAny::getId).collect(Collectors.toList()))
                .stream()
                .filter(Objects::nonNull)
                .map(Enhet.class::cast)
                .collect(Collectors.toList());

        if (cachedEnhets.size() == enhetIds.size()) {
            LOG.info("All enhets cached");
            return cachedEnhets;
        }

        final Set<HsaIdEnhet> hsaIdEnhetsNotInCache = Sets.newHashSet(enhetIds);
        final List<HsaIdEnhet> hsaIdsInCache = cachedEnhets.stream().map(Enhet::getEnhetId).collect(Collectors.toList());
        hsaIdEnhetsNotInCache.removeAll(hsaIdsInCache);

        final Set<Enhet> enhets = loader.apply(hsaIdEnhetsNotInCache).stream()
                .peek(e -> hashOps.put(ENHET, e.getEnhetId().getId(), e))
                .collect(Collectors.toSet());

        enhets.addAll(cachedEnhets);

        LOG.info("All enhets not cached");
        return enhets;
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

    // returns a typed object from cache, or loads a value if no such hashKey exists
    private <T>  T lookup(final Object key, final Object hashKey, final Supplier<T> loader) {
        final HashOperations<Object, Object, Object> ops = template.opsForHash();

        if (ops.hasKey(key, hashKey)) {
            try {
                return (T) ops.get(key, hashKey);
            } catch (ClassCastException e) {
                LOG.warn("Failed to cast {} object in cache: {}", key, e.getMessage());
            }
        }

        final T value = loader.get();
        ops.put(key, hashKey, value);

        return value;
    }

    /**
     * Gets a typed object from cache if existing and sets a new value for the same key.
     * @return The old value if it existed, else the new value.
     */
    private <T>  T getAndSet(final Object key, final Object hashKey, final Supplier<T> loader) {
        final HashOperations<Object, Object, Object> ops = template.opsForHash();
        T t = null;
        try {
            t = (T) ops.get(key, hashKey);
        } catch (ClassCastException e) {
            LOG.warn("Failed to cast {} object in cache: {}", key, e.getMessage());
        }

        final T value = loader.get();
        ops.put(key, hashKey, value);

        return t;
    }

    private Aisle aisleLoader(HsaIdVardgivare vardgivarId, Function<HsaIdVardgivare, Aisle> loader) {
        LOG.info("Aisle not cached: {}", vardgivarId);
        return loader.apply(vardgivarId);
    }

    private List<HsaIdEnhet> loadVgEnhets(HsaIdVardgivare vardgivareId, Function<HsaIdVardgivare, List<Enhet>> loader) {
        LOG.info("VgEnhets not cached: {}", vardgivareId);
        final HashOperations<Object, Object, Object> hashOps = template.opsForHash();
        return loader.apply(vardgivareId).stream()
                .peek(e -> hashOps.put(ENHET, e.getEnhetId().getId(), e))
                .map(Enhet::getEnhetId)
                .collect(Collectors.toList());
    }

    public <T> T getNationalData(Supplier<T> supplier) {
        LOG.info("Getting national data");
        return lookup(NATIONAL_DATA, NATIONAL_DATA, supplier);
    }

    public synchronized boolean getAndSetNationaldataCalculationOngoing(boolean isOngoing) {
        LOG.info("Getting and setting ongoing national data calculation");

        final HashOperations<Object, Object, Object> ops = template.opsForHash();

        final String hashKey = "ONGOING_CALCULATION";
        final String key = NATIONAL_DATA;
        if (!isOngoing) {
            template.delete(key);
            return false;
        }

        final Object existingValue = ops.get(key, hashKey);
        ops.put(key, hashKey, true);
        if (existingValue == null) {
            template.expire(key, ONE_HOUR_IN_MILLIS, TimeUnit.MILLISECONDS);
            return false;
        }

        return true;
    }

    public Collection<IntygType> getAllExistingIntygTypes(Supplier<Collection<IntygType>> supplier) {
        LOG.info("Getting all existing intyg types");
        return lookup(EXISTING_INTYGTYPES, NATIONAL_DATA, supplier);
    }

}
