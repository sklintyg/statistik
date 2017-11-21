/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Warehouse implements Iterable<Aisle> {

    private static final Logger LOG = LoggerFactory.getLogger(Warehouse.class);

    @Autowired
    private WidelineLoader widelineLoader;

    @Autowired
    private EnhetLoader enhetLoader;

    private static IdMap<HsaIdEnhet> enhetsMap = new IdMap<>();
    private static IdMap<HsaIdLakare> lakareMap = new IdMap<>();

    public Aisle get(HsaIdVardgivare vardgivarId) {
        final List<Aisle> ailesForVgs = widelineLoader.getAilesForVgs(Collections.singletonList(vardgivarId));
        return ailesForVgs.isEmpty() ? new Aisle(vardgivarId, Collections.emptyList()) : ailesForVgs.get(0);
    }

    public List<Enhet> getEnhets(HsaIdVardgivare vardgivareId) {
        return enhetLoader.getAllEnhetsForVg(vardgivareId);
    }

    public List<Enhet> getEnhetsWithHsaId(Collection<HsaIdEnhet> enhetIds) {
        return enhetLoader.getEnhets(enhetIds);
    }

    public List<HsaIdVardgivare> getAllVardgivare() {
        return widelineLoader.getAllVgs();
    }

    @NotNull
    @Override
    public Iterator<Aisle> iterator() {
        return new Iterator<Aisle>() {
            private static final int BATCH_SIZE = 10;
            private int nextIndex = 0;
            private List<HsaIdVardgivare> allVardgivare = getAllVardgivare();
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
                final int toIndex = Math.min(fromIndex + BATCH_SIZE, allVardgivare.size());
                final List<HsaIdVardgivare> vgids = allVardgivare.subList(fromIndex, toIndex);
                return widelineLoader.getAilesForVgs(vgids).iterator();
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
