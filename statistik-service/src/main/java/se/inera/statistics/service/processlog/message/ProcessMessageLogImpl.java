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
package se.inera.statistics.service.processlog.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.processlog.AbstractProcessLog;
import se.inera.statistics.service.processlog.ProcessLogImpl;

import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class ProcessMessageLogImpl extends AbstractProcessLog implements ProcessMessageLog {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessLogImpl.class);

    public ProcessMessageLogImpl() {
        super("PROCESSED_MESSAGE");
    }

    @Override
    @Transactional
    public long store(MessageEventType type, String data, String messageId, long timestamp) {
        TypedQuery<MessageEvent> select = getManager().createQuery("SELECT e FROM MessageEvent e WHERE e.correlationId = :correlationId AND e.type = :type", MessageEvent.class);
        select.setParameter("correlationId", messageId).setParameter("type", type);
        List<MessageEvent> result = select.getResultList();
        if (result.isEmpty()) {
            MessageEvent event = new MessageEvent(type, data, messageId, timestamp);
            getManager().persist(event);
            return event.getId();
        } else {
            LOG.info("Message already exists, ignoring: " + messageId);
            return result.get(0).getId();
        }
    }

    @Override
    public void confirm(long id) {
        confirmId(id);
    }

    @Override
    @Transactional
    public List<MessageEvent> getPending(int max) {
        TypedQuery<MessageEvent> allQuery = getManager().createQuery("SELECT e from MessageEvent e WHERE e.id > :lastId ORDER BY e.id ASC", MessageEvent.class);
        allQuery.setParameter("lastId", getLastId());
        allQuery.setMaxResults(max);
        return allQuery.getResultList();
    }
}
