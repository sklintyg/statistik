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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.SendMessageToCareHelper;
import se.inera.statistics.service.processlog.message.MessageEventType;
import se.inera.statistics.service.warehouse.AbstractWidlineConverter;
import se.inera.statistics.service.warehouse.model.db.MessageWideLine;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageWidelineConverter extends AbstractWidlineConverter {

    private static final int MAX_LENGTH_MESSAGE_ID = 50;

    @Autowired
    private SendMessageToCareHelper sendMessageToCareHelper;

    public List<String> validate(MessageWideLine line) {
        List<String> errors = new ArrayList<>();
        checkField(errors, line.getPatientid(), "Patient");
        checkField(errors, line.getMeddelandeId(), "MeddelandeId", MAX_LENGTH_MESSAGE_ID);
        return errors;
    }

    public MessageWideLine toWideline(SendMessageToCareType meddelande, Patientdata patientdata, long logId, String meddelandeId, MessageEventType type) {
        MessageWideLine line = new MessageWideLine();

        line.setMeddelandeId(meddelandeId);
        line.setMeddelandeTyp(type);
        line.setIntygsId(sendMessageToCareHelper.getIntygId(meddelande));
        line.setPatientid(sendMessageToCareHelper.getPatientId(meddelande));
        line.setAmneCode(sendMessageToCareHelper.getAmneCode(meddelande));
        line.setSkickatAv(sendMessageToCareHelper.getSkickatAv(meddelande));
        LocalDateTime dateTime = sendMessageToCareHelper.getSkickatTidpunkt(meddelande);
        line.setSkickatTidpunkt(dateTime.toLocalTime());
        line.setSkickatDate(dateTime.toLocalDate());
        line.setLogId(logId);
        line.setKon(patientdata.getKon().getNumberRepresentation());
        line.setAlder(patientdata.getAlder());

        return line;
    }
}
