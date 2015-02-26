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
package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.warehouse.WidelineManager;

public class Processor {
    @Autowired
    private WidelineManager widelineManager;

    @Autowired
    private VardgivareManager vardgivareManager;

    @Autowired
    private LakareManager lakareManager;

    private int processedCounter;

    public void accept(JsonNode utlatande, JsonNode hsa, long logId, String correlationId, EventType type) {
        ObjectNode preparedDoc = DocumentHelper.prepare(utlatande);

        vardgivareManager.saveEnhet(hsa, preparedDoc);

        lakareManager.saveLakare(hsa);

        widelineManager.accept(preparedDoc, hsa, logId, correlationId, type);

        processedCounter++;
    }

    public int getProcessedCounter() {
        return processedCounter;
    }
}
