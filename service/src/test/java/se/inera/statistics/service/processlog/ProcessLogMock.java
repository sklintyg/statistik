/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

public class ProcessLogMock implements ProcessLog {

    private static final String PROCESSED_HSA = "PROCESSED_HSA";

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Override
    @Transactional
    public long store(EventType type, String data, String correlationId, long timestamp) {
        throw new RuntimeException("This is a runtime exception that causes a rollback of the jdbc transaction");
    }

    @Transactional
    public IntygEvent get(long id) {
        return manager.find(IntygEvent.class, id);
    }

    @Override
    @Transactional
    public List<IntygEvent> getPending(int max) {
        TypedQuery<IntygEvent> allQuery = manager
            .createQuery("SELECT e from IntygEvent e WHERE e.id > :lastId ORDER BY e.id ASC", IntygEvent.class);
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
