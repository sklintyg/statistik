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

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.warehouse.IntygCommonManager;
import se.inera.statistics.service.warehouse.WidelineManager;
import se.inera.statistics.service.warehouse.message.MessageWidelineManager;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

public class Processor {
    @Autowired
    private WidelineManager widelineManager;

    @Autowired
    private IntygCommonManager intygCommonManager;

    @Autowired
    private MessageWidelineManager messageWidelineManagar;

    @Autowired
    private VardgivareManager vardgivareManager;

    @Autowired
    private LakareManager lakareManager;

    @Autowired
    private SendMessageToCareHelper sendMessageToCareHelper;

    public void accept(IntygDTO dto, HsaInfo hsa, long logId, String correlationId, EventType type) {
        final String enhetId = dto.getEnhet();
        saveEnhetAndLakare(hsa, enhetId);
        handleWithIntygCommonManager(dto, hsa, logId, correlationId, type);
        String intygTyp = dto.getIntygtyp().toUpperCase();
        switch (intygTyp) {
        case "LISU":
        case "LISJP":
        case "FK7263":
        case "LIS":
            handleWithWidelineManager(dto, hsa, logId, correlationId, type);
            break;
        default:
            // do nothing
        }
    }

    /**
     * @param dto
     * @param hsa
     * @param logId
     * @param correlationId
     * @param type
     */
    private void handleWithIntygCommonManager(IntygDTO dto, HsaInfo hsa, long logId, String correlationId, EventType type) {
        intygCommonManager.accept(dto, hsa, logId, correlationId, type);
    }

    private void handleWithWidelineManager(IntygDTO dto, HsaInfo hsa, long logId, String correlationId, EventType type) {
        widelineManager.accept(dto, hsa, logId, correlationId, type);
    }

    public void accept(SendMessageToCareType message, long logId, String messageId, MessageEventType type) {

        final Patientdata patientData = sendMessageToCareHelper.getPatientData(message);
        messageWidelineManagar.accept(message, patientData, logId, messageId, type);
    }

    private void saveEnhetAndLakare(HsaInfo hsa, String enhetId) {
        vardgivareManager.saveEnhet(hsa, enhetId);
        lakareManager.saveLakare(hsa);
    }

}
