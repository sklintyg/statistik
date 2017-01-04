/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdLakare;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.processlog.Enhet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Warehouse implements Iterable<Aisle> {

    private static final Logger LOG = LoggerFactory.getLogger(Warehouse.class);

    private volatile Map<HsaIdVardgivare, Aisle> aisles = new HashMap<>();
    private Map<HsaIdVardgivare, MutableAisle> loadingAisles = new HashMap<>();
    private volatile Map<HsaIdVardgivare, List<Enhet>> enhets;
    private Map<HsaIdVardgivare, List<Enhet>> loadingEnhets = new HashMap<>();
    private static IdMap<HsaIdEnhet> enhetsMap = new IdMap<>();
    private static IdMap<HsaIdLakare> lakareMap = new IdMap<>();
    private LocalDateTime lastUpdate = null;

    public void accept(Fact fact, HsaIdVardgivare vardgivareId) {
        MutableAisle aisle = getAisle(vardgivareId, loadingAisles, true);
        aisle.addLine(fact);
    }

    public void accept(Enhet enhet) {
        List<Enhet> list = loadingEnhets.get(enhet.getVardgivareId());
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(enhet);
        loadingEnhets.put(enhet.getVardgivareId(), list);
    }

    public Aisle get(HsaIdVardgivare vardgivarId) {
        final Aisle aisle = aisles.get(vardgivarId);
        if (aisle == null) {
            return new MutableAisle(vardgivarId).createAisle();
        }
        return aisle;
    }

    public List<Enhet> getEnhets(HsaIdVardgivare vardgivareId) {
        List<Enhet> result = enhets.get(vardgivareId);
        return result == null ? new ArrayList<Enhet>() : result;
    }

    public List<Enhet> getEnhetsWithHsaId(Collection<HsaIdEnhet> enhetIds) {
        if (enhets == null || enhetIds == null) {
            return new ArrayList<>();
        }
        return enhets.values().stream().reduce(Lists.newArrayList(), (a, b) -> {
            a.addAll(b);
            return a;
        }).stream().filter(enhet -> enhetIds.contains(enhet.getEnhetId())).collect(Collectors.toList());
    }

    private MutableAisle getAisle(HsaIdVardgivare vardgivareId, Map<HsaIdVardgivare, MutableAisle> aisles, boolean add) {
        MutableAisle aisle = aisles.get(vardgivareId);
        if (aisle == null) {
            aisle = new MutableAisle(vardgivareId);
            if (add) {
                aisles.put(vardgivareId, aisle);
            }
        }
        return aisle;
    }

    @Override
    public String toString() {
        long total = 0;
        for (Aisle aisle: aisles.values()) {
            total += aisle.getSize();
        }
        return "Warehouse [" + aisles.size() + " aisles, " + total + " lines]";
    }

    public Map<HsaIdVardgivare, Aisle> getAllVardgivare() {
        return aisles;
    }

    @Override
    public Iterator<Aisle> iterator() {
        return aisles.values().iterator();
    }

    public static int getEnhetAndRemember(HsaIdEnhet id) {
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void complete(LocalDateTime lastUpdate) {
        Map<HsaIdVardgivare, Aisle> newAisles = new HashMap<>();
        for (Map.Entry<HsaIdVardgivare, MutableAisle> entry : loadingAisles.entrySet()) {
            newAisles.put(entry.getKey(), entry.getValue().createAisle());
        }
        aisles = Collections.unmodifiableMap(newAisles);
        this.lastUpdate = lastUpdate;
        loadingAisles = new HashMap<>();
    }

    public void completeEnhets() {
        for (List<Enhet> enhetList: loadingEnhets.values()) {
            Collections.sort(enhetList, Enhet.byEnhetId());
        }
        enhets = Collections.unmodifiableMap(loadingEnhets);
        loadingEnhets = new HashMap<>();
    }

    public void clear() {
        LOG.warn("Clearing warehouse ailes");
        loadingAisles.clear();
        complete(LocalDateTime.now());
    }

    private static class IdMap<T> {
        private final BiMap<T, Integer> map = HashBiMap.create();

        public synchronized Integer getOrCreateId(T key) {
            Integer id = map.get(key);
            if (id == null) {
                id = map.size() + 1;
                map.put(key, id);
            }
            return id;
        }

        public synchronized Integer maybeGetId(T key) {
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
                return Optional.absent();
            }
            return Optional.of(result);
        }

        public synchronized Map<T, Integer> getView() {
            return Collections.unmodifiableMap(map);
        }

    }

}
