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
package se.inera.statistics.service.processlog.intygsent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.processlog.AbstractProcessLog;

import javax.persistence.TypedQuery;
import java.util.List;

@Component
public class ProcessIntygsentLogImpl extends AbstractProcessLog implements ProcessIntygsentLog {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessIntygsentLogImpl.class);

    public ProcessIntygsentLogImpl() {
        super("PROCESSED_INTYGSENT");
    }

    @Override
    @Transactional
    public long store(String certificateId, String certificateRecipient, long timestamp) {
        final IntygSentEvent event = new IntygSentEvent(certificateId, certificateRecipient, timestamp);
        getManager().merge(event);
        return event.getId();
    }

    @Override
    @Transactional
    public long update(IntygSentEvent event) {
        getManager().persist(event);
        return event.getId();
    }

    @Override
    @Transactional
    public List<IntygSentEvent> getPending(int max) {
        String query = "SELECT e FROM IntygSentEvent e WHERE e.id > :lastId ORDER BY e.id ASC";

        TypedQuery<IntygSentEvent> allQuery = getManager().createQuery(query, IntygSentEvent.class);
        allQuery.setParameter("lastId", getLastId());
        allQuery.setMaxResults(max);
        return allQuery.getResultList();
    }

    @Override
    public void confirm(long id) {
        confirmId(id);
    }

}
