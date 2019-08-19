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
package se.inera.statistics.service.warehouse;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.warehouse.model.db.WideLine;

@Component
public class WidelineManager {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineManager.class);

    private static int errCount;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private WidelineConverter widelineConverter;

    private void persistIfValid(long logId, String intygid, WideLine line) {
        List<String> errors = widelineConverter.validate(line);

        if (errors.isEmpty()) {
            saveWideline(line);
        } else {
            StringBuilder errorBuilder = new StringBuilder("Faulty intyg logid ").append(logId).append(" id ").append(intygid)
                .append(" error count ").append(errCount++);
            for (String error : errors) {
                errorBuilder.append('\n').append(error);
            }
            LOG.error(errorBuilder.toString());
        }
    }

    @Transactional(noRollbackFor = Exception.class)
    public void accept(IntygDTO dto, HsaInfo hsa, long logId, String correlationId, EventType type) {
        final String intygid = dto.getIntygid();
        final IntygType intygtyp = dto.getIntygtyp();
        if (!intygtyp.isSupportedIntyg()) {
            LOG.info("Intygtype not supported. Ignoring intyg: " + intygid);
            return;
        }
        for (WideLine line : widelineConverter.toWideline(dto, hsa, logId, correlationId, type)) {
            persistIfValid(logId, intygid, line);
        }
    }

    @Transactional
    public void saveWideline(WideLine line) {
        manager.persist(line);
        if (revokeCorrelationId(line)) {
            inactivateIntyg(line.getCorrelationId());
        }
    }

    private boolean revokeCorrelationId(WideLine line) {
        return EventType.REVOKED.equals(line.getIntygTyp()) || isCorrelationIdAlreadyRevoked(line.getCorrelationId());
    }

    private boolean isCorrelationIdAlreadyRevoked(String correlationId) {
        final int revokedOrdinal = EventType.REVOKED.ordinal();
        final Query query = manager.createQuery("SELECT intygTyp, correlationId FROM WideLine "
            + "WHERE intygTyp = " + revokedOrdinal + " AND correlationId = :corrId");
        query.setParameter("corrId", correlationId);
        return !query.getResultList().isEmpty();
    }

    @Transactional
    public int count() {
        return ((Long) manager.createQuery("SELECT COUNT (wl) FROM WideLine wl").getSingleResult()).intValue();
    }

    private void inactivateIntyg(String correlationId) {
        final Query query = manager.createQuery("UPDATE WideLine SET active = false WHERE correlationId = :corrId");
        query.setParameter("corrId", correlationId);
        query.executeUpdate();
    }

}
