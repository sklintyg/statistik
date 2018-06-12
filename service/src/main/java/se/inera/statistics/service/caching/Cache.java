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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdAny;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is a common cache used to get better performance in ST.
 * It is a shared cache to make it easier to maintain the memory footprint.
 */
@Component
public class Cache {
    private static final Logger LOG = LoggerFactory.getLogger(Cache.class);
    private static final String REDIS_KEY_PREFIX = "INTYGSSTATISTIK_";
    private static final String AISLE = REDIS_KEY_PREFIX + "AISLE_";
    private static final String SJUKFALLGROUP = REDIS_KEY_PREFIX + "SJUKFALLGROUP_";
    private static final String VGENHET = REDIS_KEY_PREFIX + "VGENHET_";
    private static final String ENHET = REDIS_KEY_PREFIX + "ENHET_";

    @Value("${cache.maxsize:1000}")
    private String maxSize;

    @Autowired
    private RedisTemplate<String, Object> template;

    public Cache() {
    }

    /**
     * Used by tests.
     */
    public Cache(RedisTemplate<String, Object> template, String maxSize) {
        this.template = template;
        this.maxSize = maxSize;
    }

    @Scheduled(cron = "${scheduler.factReloadJob.cron}")
    public void clearCaches() {
        final Set<String> keys = template.keys(REDIS_KEY_PREFIX + "*");
        if (keys != null) {
            template.delete(keys);
        } else {
            LOG.warn("ClearCache was requested but no value was found in cache");
        }
    }

    public List<SjukfallGroup> getSjukfallGroups(SjukfallGroupCacheKey key) {
        LOG.info("Getting sjukfallgroups: {}", key.getKey());
        final HashOperations<String, String, Object> hashOps = template.opsForHash();
        final Boolean hasKey = hashOps.hasKey(SJUKFALLGROUP, key.getKey());
        if (hasKey) { //Spotbugs did not allow a null check here, I'm not sure why
            try {
                final Object group = hashOps.get(SJUKFALLGROUP, key.getKey());
                return (List<SjukfallGroup>) group;
            } catch (ClassCastException e) {
                LOG.warn("Failed to cast sjukfallsgroup in cache");
                hashOps.delete(SJUKFALLGROUP, key.getKey());
            }
        }
        final List<SjukfallGroup> sjukfallGroups = loadSjukfallgroups(key);
        hashOps.put(SJUKFALLGROUP, key.getKey(), sjukfallGroups);
        return sjukfallGroups;
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
        final String key = vardgivarId.getId();
        final HashOperations<String, Object, Object> hashOps = template.opsForHash();
        if (hashOps.hasKey(AISLE, key)) {
            try {
                final Object aisle = hashOps.get(AISLE, key);
                return (Aisle) aisle;
            } catch (ClassCastException e) {
                LOG.warn("Failed to cast aisle in cache");
                hashOps.delete(AISLE, key);
            }
        }
        final Aisle facts = aisleLoader(vardgivarId, loader);
        hashOps.put(AISLE, key, facts);
        return facts;
    }

    private Aisle aisleLoader(HsaIdVardgivare vardgivarId, Function<HsaIdVardgivare, Aisle> loader) {
        LOG.info("Aisle not cached: {}", vardgivarId);
        return loader.apply(vardgivarId);
    }

    private List<HsaIdEnhet> loadVgEnhets(HsaIdVardgivare vardgivareId, Function<HsaIdVardgivare, List<Enhet>> loader) {
        LOG.info("VgEnhets not cached: {}", vardgivareId);
        final List<Enhet> allEnhetsForVg = loader.apply(vardgivareId);
        final HashOperations<String, Object, Object> hashOps = template.opsForHash();
        for (Enhet enhet : allEnhetsForVg) {
            hashOps.put(ENHET, enhet.getEnhetId().getId(), enhet);
        }
        return allEnhetsForVg.stream().map(Enhet::getEnhetId).collect(Collectors.toList());
    }

    public List<HsaIdEnhet> getVgEnhets(HsaIdVardgivare vardgivareId, Function<HsaIdVardgivare, List<Enhet>> loader) {
        LOG.info("Getting VgEnhets: {}", vardgivareId);
        final String key = vardgivareId.getId();
        final HashOperations<String, Object, Object> hashOps = template.opsForHash();
        if (hashOps.hasKey(VGENHET, key)) {
            try {
                final Object enhets = hashOps.get(VGENHET, key);
                return (List<HsaIdEnhet>) enhets;
            } catch (ClassCastException e) {
                LOG.warn("Failed to cast vgenhets in cache");
                hashOps.delete(VGENHET, key);
            }
        }
        final List<HsaIdEnhet> hsaIdEnhets = loadVgEnhets(vardgivareId, loader);
        hashOps.put(VGENHET, key, hsaIdEnhets);
        return hsaIdEnhets;
    }

    public Collection<Enhet> getEnhetsWithHsaId(Collection<HsaIdEnhet> enhetIds,
                                                Function<Collection<HsaIdEnhet>, Collection<Enhet>> loader) {
        final HashOperations<String, String, Object> hashOps = template.opsForHash();
        final List<Object> cachedEnhetsObj = hashOps.multiGet(ENHET, enhetIds.stream().map(HsaIdAny::getId).collect(Collectors.toList()));
        final List<Enhet> cachedEnhets = cachedEnhetsObj.stream().filter(o -> o != null).map(o -> (Enhet) o).collect(Collectors.toList());
        if (cachedEnhets.size() == enhetIds.size()) {
            LOG.info("All enhets cached");
            return cachedEnhets;
        }
        final HashSet<HsaIdEnhet> hsaIdEnhetsNotInCache = new HashSet<>(enhetIds);
        final List<HsaIdEnhet> hsaIdsInCache = cachedEnhets.stream().map(Enhet::getEnhetId).collect(Collectors.toList());
        hsaIdEnhetsNotInCache.removeAll(hsaIdsInCache);
        final Set<Enhet> enhets = new HashSet<>(loader.apply(hsaIdEnhetsNotInCache));
        for (Enhet enhet : enhets) {
            hashOps.put(ENHET, enhet.getEnhetId().getId(), enhet);
        }
        enhets.addAll(cachedEnhets);
        LOG.info("All enhets not cached");
        return enhets;
    }

}
