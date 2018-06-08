/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;

import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.IntygDTO;

public abstract class IntygHelper<T> {

    private Unmarshaller jaxbUnmarshaller;

    public synchronized T unmarshalXml(String data) throws JAXBException {
        final StringReader reader = new StringReader(data);
        return (T) JAXBIntrospector.getValue(getUnmarshaller().unmarshal(reader));
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        if (jaxbUnmarshaller == null) {
            jaxbUnmarshaller = JAXBContext.newInstance(getIntygClass()).createUnmarshaller();
            jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        }
        return jaxbUnmarshaller;
    }

    protected abstract Class<T> getIntygClass();

    public abstract String getEnhetId(T intyg);

    public abstract String getLakareId(T intyg);

    public abstract String getVardgivareId(T intyg);

    public abstract String getPatientId(T intyg);

    public abstract String getIntygId(T intyg);

    public abstract String getIntygtyp(T intyg);

    public abstract Patientdata getPatientData(T intyg);

    public abstract LocalDateTime getSigneringsTidpunkt(T intyg);

    public HSAKey extractHSAKey(T intyg) {
        String vardgivareId = getVardgivareId(intyg);
        String enhetId = getEnhetId(intyg);
        String lakareId = getLakareId(intyg);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }

    public IntygDTO convertToDTO(T intyg) {
        if (intyg == null) {
            return null;
        }

        IntygDTO dto = new IntygDTO();

        String enhet = getEnhetId(intyg);
        String patient = getPatientId(intyg);
        Patientdata patientData = getPatientData(intyg);

        String lakareid = getLakareId(intyg);
        String intygsId = getIntygId(intyg);
        String intygTyp = getIntygtyp(intyg);
        LocalDate signeringsDatum = getSigneringsTidpunkt(intyg).toLocalDate();

        dto.setEnhet(enhet);
        dto.setIntygid(intygsId);
        dto.setIntygtyp(intygTyp);
        dto.setLakareId(lakareid);
        dto.setPatientid(patient);
        dto.setPatientData(patientData);
        dto.setSigneringsdatum(signeringsDatum);

        return dto;
    }

}
