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
package se.inera.statistics.service.warehouse.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.warehouse.WidelineManager;
import se.inera.statistics.service.warehouse.model.db.MessageWideLine;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class MessageWidelineManager {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineManager.class);

    private static int errCount;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private MessageWidelineConverter widelineConverter;

    @Autowired
    private SendMessageToCareHelper sendMessageToCareHelper;

    private void persistIfValid(long logId, String meddelandeId, MessageWideLine line) {
        List<String> errors = widelineConverter.validate(line);

        if (errors.isEmpty()) {
            saveWideline(line);
        } else {
            String errorString = "Faulty meddelande logid " + logId + " id " + meddelandeId + " error count " + errCount;

            for (String error : errors) {
                errorString += "\n" + error;
            }
            LOG.error(errorString);

            errCount++;
        }
    }

    @Transactional(noRollbackFor = Exception.class)
    public void accept(SendMessageToCareType message, Patientdata patientdata, long logId, String messageId, MessageEventType type) {
        final String intygid = sendMessageToCareHelper.getIntygId(message);

        MessageWideLine line = widelineConverter.toWideline(message, patientdata, logId, messageId, type);

        persistIfValid(logId, intygid, line);
    }

    @Transactional
    public int count() {
        return ((Long) manager.createQuery("SELECT COUNT (wl) FROM MessageWideLine wl").getSingleResult()).intValue();
    }

    @Transactional
    public void saveWideline(MessageWideLine line) {
        manager.persist(line);
    }
}
