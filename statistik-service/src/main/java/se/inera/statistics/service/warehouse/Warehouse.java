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
package se.inera.statistics.service.warehouse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Warehouse implements Iterable<Aisle> {

    private static final Logger LOG = LoggerFactory.getLogger(Warehouse.class);

    @Autowired
    private WidelineLoader widelineLoader;

    @Autowired
    private EnhetLoader enhetLoader;

    private static IdMap<HsaIdEnhet> enhetsMap = new IdMap<>();
    private static IdMap<HsaIdLakare> lakareMap = new IdMap<>();

    private LoadingCache<HsaIdVardgivare, Aisle> aisleCache;
    private LoadingCache<HsaIdVardgivare, List<HsaIdEnhet>> vgEnhetsCache;
    private Cache<HsaIdEnhet, Enhet> enhetsCache;

    @Scheduled(cron = "${scheduler.factReloadJob.cron}")
    public void clearCaches() {
        getAisleCache().invalidateAll();
        getVgEnhetsCache().invalidateAll();
        getEnhetsCache().invalidateAll();
    }

    private LoadingCache<HsaIdVardgivare, Aisle> getAisleCache() {
        if (aisleCache == null) {
            aisleCache = CacheBuilder.newBuilder()
                    .softValues()
                    .build(new CacheLoader<HsaIdVardgivare, Aisle>() {
                        @Override
                        public Aisle load(@NotNull HsaIdVardgivare vardgivarId) {
                            return getAisle(vardgivarId);
                        }
                    });
        }
        return aisleCache;
    }

    private LoadingCache<HsaIdVardgivare, List<HsaIdEnhet>> getVgEnhetsCache() {
        final long maxSize = 500L;
        if (vgEnhetsCache == null) {
            vgEnhetsCache = CacheBuilder.newBuilder()
                    .maximumSize(maxSize)
                    .build(new CacheLoader<HsaIdVardgivare, List<HsaIdEnhet>>() {
                        @Override
                        public List<HsaIdEnhet> load(@NotNull HsaIdVardgivare vardgivareId) {
                            final List<Enhet> allEnhetsForVg = enhetLoader.getAllEnhetsForVg(vardgivareId);
                            for (Enhet enhet : allEnhetsForVg) {
                                getEnhetsCache().put(enhet.getEnhetId(), enhet);
                            }
                            return allEnhetsForVg.stream().map(Enhet::getEnhetId).collect(Collectors.toList());
                        }
                    });
        }
        return vgEnhetsCache;
    }

    private Cache<HsaIdEnhet, Enhet> getEnhetsCache() {
        final long maxSize = 5000L;
        if (enhetsCache == null) {
            enhetsCache = CacheBuilder.newBuilder().maximumSize(maxSize).build();
        }
        return enhetsCache;
    }

    private Aisle getAisle(HsaIdVardgivare vardgivarId) {
        final List<Aisle> ailesForVgs = widelineLoader.getAilesForVgs(Collections.singletonList(vardgivarId));
        return ailesForVgs.isEmpty() ? new Aisle(vardgivarId, Collections.emptyList()) : ailesForVgs.get(0);
    }

    public Aisle get(HsaIdVardgivare vardgivarId) {
        try {
            return getAisleCache().get(vardgivarId);
        } catch (ExecutionException e) {
            LOG.warn("Failed to get aisle from cache for vg: " + vardgivarId);
        }
        return getAisle(vardgivarId);
    }

    public Collection<Enhet> getEnhets(HsaIdVardgivare vardgivareId) {
        try {
            final List<HsaIdEnhet> enhetIds = getVgEnhetsCache().get(vardgivareId);
            return getEnhetsWithHsaId(enhetIds);
        } catch (ExecutionException e) {
            LOG.warn("Could not get enhets from cache for vg: " + vardgivareId);
        }
        return enhetLoader.getAllEnhetsForVg(vardgivareId);
    }

    public Collection<Enhet> getEnhetsWithHsaId(Collection<HsaIdEnhet> enhetIds) {
        final Cache<HsaIdEnhet, Enhet> enhetsCache = getEnhetsCache();
        final ImmutableMap<HsaIdEnhet, Enhet> allPresent = enhetsCache.getAllPresent(enhetIds);
        if (allPresent.size() == enhetIds.size()) {
            return new ArrayList<>(allPresent.values());
        }
        final HashSet<HsaIdEnhet> hsaIdEnhetsNotInCache = new HashSet<>(enhetIds);
        hsaIdEnhetsNotInCache.removeAll(allPresent.keySet());
        final Set<Enhet> enhets = new HashSet<>(enhetLoader.getEnhets(hsaIdEnhetsNotInCache));
        for (Enhet enhet : enhets) {
            enhetsCache.put(enhet.getEnhetId(), enhet);
        }
        enhets.addAll(allPresent.values());
        return enhets;
    }

    public List<VgNumber> getAllVardgivare() {
        return widelineLoader.getAllVgs();
    }

    @NotNull
    @Override
    public Iterator<Aisle> iterator() {
        return new Iterator<Aisle>() {
            private static final int BATCH_SIZE = 50000; //At which number of intyg to stop adding vgs and query the db
            private int nextIndex = 0;
            private List<VgNumber> allVardgivare = getAllVardgivare();
            private Iterator<Aisle> batchedAisles = Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                return allVardgivare.size() > nextIndex;
            }

            @Override
            public Aisle next() {
                if (!batchedAisles.hasNext()) {
                    batchedAisles = getNextBatch();
                }
                nextIndex++;
                return batchedAisles.next();
            }

            private Iterator<Aisle> getNextBatch() {
                final int fromIndex = this.nextIndex;
                final int toIndex = getToIndex(fromIndex);
                final List<VgNumber> vgNumbers = allVardgivare.subList(fromIndex, toIndex);
                final List<HsaIdVardgivare> vgids = vgNumbers.stream().map(VgNumber::getVgid).collect(Collectors.toList());
                return widelineLoader.getAilesForVgs(vgids).iterator();
            }

            /**
             * Return the index in allVardgivare where a sublist will get at least BATCH_SIZE of intyg, but will
             * also not add any more vgs as soon as BATCH_SIZE has been reached.
             * @param fromIndex The start index
             * @return The end index
             */
            private int getToIndex(int fromIndex) {
                int toIndex = fromIndex;
                final int size = allVardgivare.size();
                for (int nrOfIntyg = 0; nrOfIntyg < BATCH_SIZE;) {
                    if (size <= toIndex) {
                        break;
                    }
                    nrOfIntyg += allVardgivare.get(toIndex).getNumber();
                    toIndex++;
                }
                return toIndex;
            }
        };
    }

    static int getEnhetAndRemember(HsaIdEnhet id) {
        return enhetsMap.getOrCreateId(id);
    }

    public static int getEnhet(HsaIdEnhet id) {
        return enhetsMap.maybeGetId(id);
    }

    public static Optional<HsaIdEnhet> getEnhetId(int enhetIntId) {
        return enhetsMap.getKey(enhetIntId);
    }

    public static Map<HsaIdEnhet, Integer> getEnhetsView() {
        return enhetsMap.getView();
    }

    public static int getNumLakarIdAndRemember(HsaIdLakare id) {
        return lakareMap.getOrCreateId(id);
    }

    public static int getNumLakarId(HsaIdLakare id) {
        return lakareMap.maybeGetId(id);
    }

    public static Optional<HsaIdLakare> getLakarId(int lakarIntId) {
        return lakareMap.getKey(lakarIntId);
    }

    private static class IdMap<T> {
        private final BiMap<T, Integer> map = HashBiMap.create();

        synchronized Integer getOrCreateId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                id = map.size() + 1;
                map.put(key, id);
            }
            return id;
        }

        synchronized Integer maybeGetId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                return -1;
            } else {
                return id;
            }
        }

        public synchronized Optional<T> getKey(int id) {
            final T result = map.inverse().get(id);
            if (result == null) {
                return Optional.empty();
            }
            return Optional.of(result);
        }

        public synchronized Map<T, Integer> getView() {
            return Collections.unmodifiableMap(map);
        }

    }

}
