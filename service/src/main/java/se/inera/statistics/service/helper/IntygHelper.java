/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;

public abstract class IntygHelper<T> {

    private static final Logger LOG = LoggerFactory.getLogger(IntygHelper.class);

    public synchronized T unmarshalXml(String data) throws JAXBException {
        final StringReader reader = new StringReader(data);
        return JAXB.unmarshal(reader, getIntygClass());
    }

    protected abstract Class<T> getIntygClass();

    public abstract String getEnhetId(T intyg);

    public abstract String getLakareId(T intyg);

    public abstract String getVardgivareId(T intyg);

    public abstract String getPatientId(T intyg);

    public abstract String getIntygId(T intyg);

    public abstract IntygType getIntygtyp(T intyg);

    public abstract LocalDate getDateForPatientAge(T intyg);

    public Patientdata getPatientData(T intyg) {
        String patientIdRaw;
        try {
            patientIdRaw = getPatientId(intyg);
        } catch (Exception ignore) {
            patientIdRaw = "?Unknown?";
        }

        int alder = ConversionHelper.NO_AGE;
        Kon kon = Kon.UNKNOWN;
        try {
            final String personId = ConversionHelper.getUnifiedPersonId(patientIdRaw);
            try {
                final LocalDate dateForPatientAge = getDateForPatientAge(intyg);
                if (dateForPatientAge != null) {
                    alder = ConversionHelper.extractAlder(personId, dateForPatientAge);
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
        IntygType intygTyp = getIntygtyp(intyg);
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
