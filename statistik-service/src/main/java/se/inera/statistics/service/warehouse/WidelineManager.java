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

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class WidelineManager {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineManager.class);

    private static int errCount;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private WidelineConverter widelineConverter;

    @Transactional(noRollbackFor = Exception.class)
    public void accept(JsonNode intyg, JsonNode hsa, long logId, String correlationId, EventType type) {
        for (WideLine line : widelineConverter.toWideline(intyg, hsa, logId, correlationId, type)) {
            List<String> errors = widelineConverter.validate(line);

            if (errors.isEmpty()) {
                manager.persist(line);
            } else {
                String intygid = DocumentHelper.getIntygId(intyg, DocumentHelper.getIntygVersion(intyg));
                StringBuilder errorBuilder = new StringBuilder("Faulty intyg logid ").append(logId).append(" id ").append(intygid).append(" error count ").append(errCount++);
                for (String error : errors) {
                    errorBuilder.append('\n').append(error);
                }
                LOG.error(errorBuilder.toString());
            }
        }
    }

    @Transactional
    public void saveWideline(WideLine line) {
        manager.persist(line);
    }

    @Transactional
    public int count() {
        return ((Long) manager.createQuery("SELECT COUNT (wl) FROM WideLine wl").getSingleResult()).intValue();
    }
}
