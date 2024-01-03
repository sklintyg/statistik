/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog.message;

import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.processlog.AbstractProcessLog;

@Component
public class ProcessMessageLogImpl extends AbstractProcessLog implements ProcessMessageLog {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessMessageLogImpl.class);

    public ProcessMessageLogImpl() {
        super("PROCESSED_MESSAGE");
    }

    @Override
    @Transactional
    public long store(MessageEventType type, String data, String messageId, long timestamp) {
        TypedQuery<MessageEvent> select = getManager()
            .createQuery("SELECT e FROM MessageEvent e WHERE e.correlationId = :correlationId AND e.type = :type",
                MessageEvent.class);
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
    @Transactional
    public long update(MessageEvent event) {
        getManager().persist(event);
        return event.getId();
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public long increaseNumberOfTries(long logid) {
        Query select = getManager().createQuery("UPDATE MessageEvent e SET e.tries = e.tries + 1 WHERE e.id = :logId");
        select.setParameter("logId", logid);
        return select.executeUpdate();
    }

    @Override
    public long setProcessed(long logId) {
        Query select = getManager().createQuery("UPDATE MessageEvent e SET e.processed = 1 WHERE e.id = :logId");
        select.setParameter("logId", logId);
        return select.executeUpdate();
    }

    @Override
    @Transactional
    public List<MessageEvent> getPending(int max, long firstId, int maxNumberOfTries) {
        String query = "SELECT e FROM MessageEvent e WHERE e.id > :lastId AND e.tries <= :maxTries AND e.processed = 0 ORDER BY e.id ASC";

        TypedQuery<MessageEvent> allQuery = getManager().createQuery(query, MessageEvent.class);
        allQuery.setParameter("lastId", firstId);
        allQuery.setParameter("maxTries", maxNumberOfTries);

        allQuery.setMaxResults(max);
        return allQuery.getResultList();
    }
}
