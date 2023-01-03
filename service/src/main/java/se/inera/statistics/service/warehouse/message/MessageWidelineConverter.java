/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.warehouse.AbstractWidlineConverter;
import se.inera.statistics.service.warehouse.IntygCommonManager;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.inera.statistics.service.warehouse.model.db.MessageWideLine;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v2.SendMessageToCareType;

@Component
public class MessageWidelineConverter extends AbstractWidlineConverter {

    private static final int MAX_LENGTH_MESSAGE_ID = 50;
    private static final int MAX_LENGTH_INTYG_ID = 50;
    private static final int MAX_LENGTH_INTYGSTYPE = 20;
    private static final int MAX_LENGTH_VGID = 50;

    @Autowired
    private SendMessageToCareHelper sendMessageToCareHelper;
    @Autowired
    private IntygCommonManager intygCommonManager;

    public List<String> validate(MessageWideLine line) {
        List<String> errors = new ArrayList<>();
        checkField(errors, line.getVardgivareid(), "Vårdgivare", MAX_LENGTH_VGID);
        checkField(errors, line.getEnhet(), "Enhet");
        checkField(errors, line.getPatientid(), "Patient");
        checkField(errors, line.getMeddelandeId(), "MeddelandeId", MAX_LENGTH_MESSAGE_ID);
        checkField(errors, line.getIntygId(), "IntygId", MAX_LENGTH_INTYG_ID);
        checkField(errors, line.getIntygstyp(), "intygstyp", MAX_LENGTH_INTYGSTYPE);
        return errors;
    }

    public MessageWideLine toWideline(SendMessageToCareType meddelande, Patientdata patientdata, long logId, String meddelandeId,
        MessageEventType type) {
        MessageWideLine line = new MessageWideLine();

        line.setMeddelandeId(meddelandeId);
        line.setMeddelandeTyp(type);
        line.setIntygId(sendMessageToCareHelper.getIntygId(meddelande));
        line.setPatientid(sendMessageToCareHelper.getPatientId(meddelande));
        line.setAmneCode(sendMessageToCareHelper.getAmneCode(meddelande));
        LocalDateTime dateTime = sendMessageToCareHelper.getSkickatTidpunkt(meddelande);
        line.setSkickatTidpunkt(dateTime.toLocalTime());
        line.setSkickatDate(dateTime.toLocalDate());
        line.setLogId(logId);
        line.setKon(patientdata.getKon().getNumberRepresentation());
        line.setAlder(patientdata.getAlder());
        line.setSvarIds(getSvarIds(meddelande));

        IntygCommon intygCommon = intygCommonManager.getOne(line.getIntygId());

        if (intygCommon != null) {
            line.setEnhet(intygCommon.getEnhet());
            line.setVardenhet(intygCommon.getVardenhet());
            line.setVardgivareid(intygCommon.getVardgivareId());
            line.setIntygstyp(intygCommon.getIntygtyp());
            line.setIntygSigneringsdatum(intygCommon.getSigneringsdatum());
            line.setIntygDx(intygCommon.getDx());
            line.setIntygLakareId(intygCommon.getLakareId());
        }

        return line;
    }

    private String getSvarIds(SendMessageToCareType meddelande) {
        if (meddelande == null) {
            return null;
        }
        final List<SendMessageToCareType.Komplettering> komplettering = meddelande.getKomplettering();
        if (komplettering == null) {
            return null;
        }
        return komplettering.stream().map(SendMessageToCareType.Komplettering::getFrageId).collect(Collectors.joining(","));
    }
}
