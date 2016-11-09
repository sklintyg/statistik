/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractProcessLog {

    private final String PROCESSED_ID;

    protected AbstractProcessLog(String processId) {
        PROCESSED_ID = processId;
    }

    @PersistenceContext(unitName = "IneraStatisticsLog")
    protected EntityManager manager;

    protected long getLastId() {
        EventPointer pointer = getPointerQuery();
        if (pointer == null) {
            return Long.MIN_VALUE;
        } else {
            return pointer.getEventId();
        }
    }

    private EventPointer getPointerQuery() {
        return manager.find(EventPointer.class, PROCESSED_ID);
    }

    protected void confirmId(long id) {
        EventPointer pointer = getPointerQuery();
        if (pointer == null) {
            pointer = new EventPointer(PROCESSED_ID, id);
            manager.persist(pointer);
        } else {
            pointer.setEventId(id);
            manager.merge(pointer);
        }
    }
}
