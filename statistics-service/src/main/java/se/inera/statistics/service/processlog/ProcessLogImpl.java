/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.processlog;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessLogImpl implements ProcessLog {

    private static final String PROCESSED_HSA = "PROCESSED_HSA";

    private long internalLastId = Long.MIN_VALUE;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public final long store(EventType type, String data, String correlationId, long timestamp) {
        IntygEvent event = new IntygEvent(type, data, correlationId, timestamp);
        manager.persist(event);
        return event.getId();
    }

    @Transactional
    public IntygEvent get(long id) {
        return manager.find(IntygEvent.class, id);
    }

    @Override
    @Transactional
    public List<IntygEvent> getPending(int max) {
        long lastEventId = Math.max(getLastId(), internalLastId);
        TypedQuery<IntygEvent> allQuery = manager.createQuery("SELECT e from IntygEvent e WHERE e.id > :lastId ORDER BY e.id ASC", IntygEvent.class);
        allQuery.setParameter("lastId", lastEventId);
        allQuery.setMaxResults(max);
        List<IntygEvent> result = allQuery.getResultList();
        if (result.isEmpty()) {
            internalLastId = lastEventId;
        } else {
            internalLastId = result.get(result.size() - 1).getId();
        }
        return result;
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
