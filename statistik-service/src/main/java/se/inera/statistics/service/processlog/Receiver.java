/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.hsa.HSADecorator;

import com.fasterxml.jackson.databind.JsonNode;

public class Receiver  {
    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    public static final String CREATED = "created";
    public static final String REVOKED = "revoked";
    public static final String ACTION = "action";
    public static final String CERTIFICATE_ID = "certificate-id";
    @Autowired
    private ProcessLog processLog;

    @Autowired
    private HSADecorator hsaDecorator;

    private long accepted;

    public void accept(EventType type, String data, String documentId, long timestamp) {
        processLog.store(type, data, documentId, timestamp);
        JsonNode utlatande = JSONParser.parse(data);
        hsa(documentId, utlatande);
        accepted++;
    }

    public long getAccepted() {
        return accepted;
    }

    private void hsa(String documentId, JsonNode utlatande) {
        try {
            hsaDecorator.decorate(utlatande, documentId);
        } catch (Exception e) {
            LOG.error("Failed decorating intyg {}", documentId, e.getMessage());
        }
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }
}
