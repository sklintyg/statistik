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
package se.inera.statistics.service.processlog;

import java.util.List;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessLogImpl extends AbstractProcessLog implements ProcessLog {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessLogImpl.class);

    public ProcessLogImpl() {
        super("PROCESSED_HSA");
    }

    @Override
    @Transactional
    public long store(EventType type, String data, String correlationId, long timestamp) {
        TypedQuery<IntygEvent> select = getManager()
            .createQuery("SELECT e FROM IntygEvent e WHERE e.correlationId = :correlationId AND e.type = :type", IntygEvent.class);
        select.setParameter("correlationId", correlationId).setParameter("type", type);
        List<IntygEvent> result = select.getResultList();
        if (result.isEmpty()) {
            IntygEvent event = new IntygEvent(type, data, correlationId, timestamp);
            getManager().persist(event);
            return event.getId();
        } else {
            LOG.info("Intyg already exists, ignoring: " + correlationId);
            return result.get(0).getId();
        }
    }

    @Transactional
    public IntygEvent get(long id) {
        return getManager().find(IntygEvent.class, id);
    }

    @Override
    @Transactional
    public List<IntygEvent> getPending(int max) {
        TypedQuery<IntygEvent> allQuery = getManager().createQuery("SELECT e from IntygEvent e WHERE e.id > :lastId ORDER BY e.id ASC",
            IntygEvent.class);
        allQuery.setParameter("lastId", getLastId());
        allQuery.setMaxResults(max);
        return allQuery.getResultList();
    }

    @Override
    public void confirm(long id) {
        confirmId(id);
    }
}
