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
package se.inera.statistics.service.helper;

import org.springframework.stereotype.Component;
import se.inera.intyg.common.support.common.enumerations.PartKod;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.LocalDateTime;

@Component
public class SendMessageToCareHelper {

    public SendMessageToCareType unmarshalSendMessageToCareTypeXml(String data) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(SendMessageToCareType.class).createUnmarshaller();
        jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        final StringReader reader = new StringReader(data);
        return (SendMessageToCareType) JAXBIntrospector.getValue(jaxbUnmarshaller.unmarshal(reader));
    }

    public String getIntygId(SendMessageToCareType message) {
        return message.getIntygsId().getExtension();
    }

    public PartKod getSkickatAv(SendMessageToCareType message) {
        return PartKod.valueOf(message.getSkickatAv().getPart().getCode());
    }

    public LocalDateTime getSkickatTidpunkt(SendMessageToCareType message) {
        return message.getSkickatTidpunkt();
    }

    public String getPatientId(SendMessageToCareType message) {
        return message.getPatientPersonId().getExtension();
    }

    public String getAmneCode(SendMessageToCareType message) {
        return message.getAmne().getCode();
    }
}
