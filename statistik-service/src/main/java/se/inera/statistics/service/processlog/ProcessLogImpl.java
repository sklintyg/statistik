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
package se.inera.statistics.service.processlog;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessLogImpl implements ProcessLog {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessLogImpl.class);

    private static final String PROCESSED_HSA = "PROCESSED_HSA";

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public final long store(EventType type, String data, String correlationId, long timestamp) {
        TypedQuery<IntygEvent> select = manager.createQuery("SELECT e FROM IntygEvent e WHERE e.correlationId = :correlationId AND e.type = :type", IntygEvent.class);
        select.setParameter("correlationId", correlationId).setParameter("type", type);
        List<IntygEvent> result = select.getResultList();
        if (result.isEmpty()) {
            IntygEvent event = new IntygEvent(type, data, correlationId, timestamp);
            manager.persist(event);
            return event.getId();
        } else {
            LOG.info("Intyg already exists, ignoring: " + correlationId);
            return result.get(0).getId();
        }
    }

    @Transactional
    public IntygEvent get(long id) {
        return manager.find(IntygEvent.class, id);
    }

    @Override
    @Transactional
    public List<IntygEvent> getPending(int max) {
        TypedQuery<IntygEvent> allQuery = manager.createQuery("SELECT e from IntygEvent e WHERE e.id > :lastId ORDER BY e.id ASC", IntygEvent.class);
        allQuery.setParameter("lastId", getLastId());
        allQuery.setMaxResults(max);
        return allQuery.getResultList();
    }

    private long getLastId() {
        EventPointer pointer = getPointerQuery();
        if (pointer == null) {
            return Long.MIN_VALUE;
        } else {
            return pointer.getEventId();
        }
    }

    public void confirm(long id) {
        EventPointer pointer = getPointerQuery();
        if (pointer == null) {
            pointer = new EventPointer(PROCESSED_HSA, id);
            manager.persist(pointer);
        } else {
            pointer.setEventId(id);
            manager.merge(pointer);
        }
    }

    private EventPointer getPointerQuery() {
        return manager.find(EventPointer.class, PROCESSED_HSA);
    }
}
