/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import se.inera.statistics.service.warehouse.IntygType;
import se.inera.statistics.service.warehouse.WidelineManager;
import se.inera.statistics.service.warehouse.message.MessageWidelineManager;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

/**
 * Process and save information to database for intyg and messages, i.e. extract and save information
 * used by statistiktjansten.
 */
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
        intygCommonManager.accept(dto, hsa, logId, correlationId, type);
        if (isSjukpenningIntyg(dto)) {
            widelineManager.accept(dto, hsa, logId, correlationId, type);
        }
    }

    private boolean isSjukpenningIntyg(IntygDTO intyg) {
        if (intyg == null) {
            return false;
        }
        return IntygType.parseString(intyg.getIntygtyp()).isSjukpenningintyg();
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
