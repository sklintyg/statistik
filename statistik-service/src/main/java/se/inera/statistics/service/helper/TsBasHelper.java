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
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.inera.intygstjanster.ts.services.RegisterTSBasResponder.v1.RegisterTSBasType;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;

@Component
public class TsBasHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TsBasHelper.class);

    private Unmarshaller jaxbUnmarshaller;

    public synchronized RegisterTSBasType unmarshalXml(String data) throws JAXBException {
        final StringReader reader = new StringReader(data);
        return (RegisterTSBasType) JAXBIntrospector.getValue(getUnmarshaller().unmarshal(reader));
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        if (jaxbUnmarshaller == null) {
            jaxbUnmarshaller = JAXBContext.newInstance(RegisterTSBasType.class).createUnmarshaller();
            jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        }
        return jaxbUnmarshaller;
    }

    public IntygDTO convertToDTO(RegisterTSBasType intyg) {
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

    public String getEnhetId(RegisterTSBasType utlatande) {
        return utlatande.getIntyg().getGrundData().getSkapadAv().getVardenhet().getEnhetsId().getExtension();
    }

    public String getLakareId(RegisterTSBasType document) {
        return document.getIntyg().getGrundData().getSkapadAv().getPersonId().getExtension();
    }

    public String getVardgivareId(RegisterTSBasType document) {
        return document.getIntyg().getGrundData().getSkapadAv().getVardenhet().getVardgivare().getVardgivarid().getExtension();
    }

    public String getPatientId(RegisterTSBasType intyg) {
        return intyg.getIntyg().getGrundData().getPatient().getPersonId().getExtension();
    }

    public String getIntygId(RegisterTSBasType intyg) {
        return intyg.getIntyg().getIntygsId();
    }

    public String getIntygtyp(RegisterTSBasType intyg) {
        return intyg.getIntyg().getIntygsTyp().trim();
    }

    public LocalDateTime getSigneringsTidpunkt(RegisterTSBasType intyg) {
        String tidpunkt = intyg.getIntyg().getGrundData().getSigneringsTidstampel();

        return LocalDateTime.parse(tidpunkt, DateTimeFormatter.ISO_DATE_TIME);
    }

    public Patientdata getPatientData(RegisterTSBasType intyg) {
        String patientIdRaw;
        try {
            patientIdRaw = getPatientId(intyg);
        } catch (Exception ignore) {
            patientIdRaw = "?Unknown?";
        }

        int alder = ConversionHelper.NO_AGE;
        Kon kon = Kon.UNKNOWN;
        try {
            final String personId = DocumentHelper.getUnifiedPersonId(patientIdRaw);
            try {
                final LocalDate signeringsTidpunkt = getSigneringsTidpunkt(intyg).toLocalDate();
                if (signeringsTidpunkt != null) {
                    alder = ConversionHelper.extractAlder(personId, signeringsTidpunkt);
                }
            } finally {
                kon = Kon.parse(ConversionHelper.extractKon(personId));
            }
        } catch (Exception e) {
            LOG.error("Could not extract age and/or gender: '{}'", patientIdRaw);
            LOG.debug("Could not extract age and/or gender: '{}'", patientIdRaw, e);
        }
        return new Patientdata(alder, kon);
    }

    public HSAKey extractHSAKey(RegisterTSBasType intyg) {
        String vardgivareId = getVardgivareId(intyg);
        String enhetId = getEnhetId(intyg);
        String lakareId = getLakareId(intyg);
        return new HSAKey(vardgivareId, enhetId, lakareId);
    }
}
