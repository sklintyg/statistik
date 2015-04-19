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

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.processlog.Enhet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Warehouse implements Iterable<Aisle> {

    private static final Logger LOG = LoggerFactory.getLogger(Warehouse.class);

    private volatile Map<String, Aisle> aisles = new HashMap<>();
    private Map<String, Aisle> loadingAisles = new HashMap<>();
    private volatile Map<String, List<Enhet>> enhets;
    private Map<String, List<Enhet>> loadingEnhets = new HashMap<>();
    private static IdMap<String> enhetsMap = new IdMap<>();
    private static IdMap<String> lakareMap = new IdMap<>();
    private LocalDateTime lastUpdate = null;
    private LocalDateTime lastEnhetUpdate = null;

    public void accept(Fact fact, String vardgivareId) {
        Aisle aisle = getAisle(vardgivareId, loadingAisles, true);
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

    public Aisle get(String vardgivarId) {
        return getAisle(vardgivarId, aisles, false);
    }

    public List<Enhet> getEnhets(String vardgivareId) {
        List<Enhet> result = enhets.get(vardgivareId);
        return result == null ? new ArrayList<Enhet>() : result;
    }

    private Aisle getAisle(String vardgivareId, Map<String, Aisle> aisles, boolean add) {
        Aisle aisle = aisles.get(vardgivareId);
        if (aisle == null) {
            aisle = new Aisle(vardgivareId);
            if (add) {
                aisles.put(vardgivareId, aisle);
            }
        }
        return aisle;
    }

    public String toString() {
        long total = 0;
        for (Aisle aisle: aisles.values()) {
            total += aisle.getSize();
        }
        return "Warehouse [" + aisles.size() + " aisles, " + total + " lines]";
    }

    public Map<String, Aisle> getAllVardgivare() {
        return aisles;
    }

    @Override
    public Iterator<Aisle> iterator() {
        return aisles.values().iterator();
    }

    public static int getEnhetAndRemember(String id) {
        return enhetsMap.getOrCreateId(id);
    }

    public static int getEnhet(String id) {
        return enhetsMap.maybeGetId(id);
    }

    public static Optional<String> getEnhetId(int enhetIntId) {
        return enhetsMap.getKey(enhetIntId);
    }

    public static int getNumLakarIdAndRemember(String id) {
        return lakareMap.getOrCreateId(id);
    }

    public static int getNumLakarId(String id) {
        return lakareMap.maybeGetId(id);
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void complete(LocalDateTime lastUpdate) {
        for (Aisle aisle: loadingAisles.values()) {
            aisle.sort();
        }
        aisles = Collections.unmodifiableMap(loadingAisles);
        this.lastUpdate = lastUpdate;
        loadingAisles = new HashMap<>();
    }

    public void completeEnhets() {
        for (List<Enhet> enhetList: loadingEnhets.values()) {
            Collections.sort(enhetList);
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
            for (Map.Entry<T, Integer> entry : map.entrySet()) {
                if (entry.getValue() == id) {
                    return Optional.of(entry.getKey());
                }
            }
            return Optional.absent();
        }

    }

}
