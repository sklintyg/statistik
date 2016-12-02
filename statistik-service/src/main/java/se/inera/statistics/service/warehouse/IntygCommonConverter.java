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
package se.inera.statistics.service.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.helper.HSAServiceHelper;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class IntygCommonConverter {

    private static final int MAX_LENGTH_CORRELATION_ID = 50;
    public static final int MAX_LENGTH_VGID = 50;
    private static final int DATE20100101 = 3653; // 365*10 + 3
    private static final int MAX_YEARS_INTO_FUTURE = 5;

    @Autowired
    private Clock clock;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    public IntygCommon toIntygCommon(IntygDTO dto, HsaInfo hsa, String correlationId,
                                     EventType type) {
        String enhet = HSAServiceHelper.getEnhetId(hsa);
        HsaIdVardgivare vardgivare = HSAServiceHelper.getVardgivarId(hsa);
        if (enhet == null) {
            enhet = dto.getEnhet();
        }
        String patient = dto.getPatientid();
        Patientdata patientData = dto.getPatientData();
        int kon = patientData.getKon().getNumberRepresentation();
        String intygTyp = dto.getIntygtyp().toUpperCase();
        LocalDate signeringsDatum = dto.getSigneringsdatum();

        return createIntygCommon(correlationId, type, enhet, vardgivare, patient, kon, intygTyp, signeringsDatum);
    }

    // FIXME: Add type to table and handle it (e.g. revoked)
    // FIXME: Checkstyle warning
    // CHECKSTYLE:OFF ParameterNumber
    private IntygCommon createIntygCommon(String correlationId, EventType type, String enhet, HsaIdVardgivare vardgivare, String patient,
                                          int kon, String intygTyp, LocalDate signeringsdatum) {
        final int signeringsdatumDay = WidelineConverter.toDay(signeringsdatum); //FIXME: Varför använda det krangliga int-systemet har?
        return new IntygCommon(correlationId, patient, signeringsdatumDay, intygTyp, enhet, vardgivare.getId(), kon);
    }
    // CHECKSTYLE:ON ParameterNumber

    public List<String> validate(IntygCommon line) {
        List<String> errors = new ArrayList<>();
        checkField(errors, line.getEnhet(), "Enhet");
        checkField(errors, line.getPatientid(), "Patient");
        checkField(errors, line.getIntygtyp(), "IntygTyp");
        checkField(errors, line.getVardgivareId(), "VardgivareId");
        checkField(errors, line.getIntygid(), "IntygId", MAX_LENGTH_CORRELATION_ID);
        checkSigneringsdatum(errors, line.getSigneringsdatum());
        return errors;
    }

    private void checkField(List<String> errors, String field, String fieldName) {
        if (field == null || field.isEmpty()) {
            errors.add(fieldName + " not found.");
        }
    }

    private void checkField(List<String> errors, String field, String fieldName, int max) {
        checkField(errors, field, fieldName);
        if (field != null && field.length() > max) {
            errors.add(fieldName + " input too long");
        }
    }

    private void checkSigneringsdatum(List<String> errors, int startdatum) {
        if (startdatum < DATE20100101 || startdatum > WidelineConverter.toDay(LocalDate.now(clock).plusYears(MAX_YEARS_INTO_FUTURE))) {
            errors.add("Illegal startdatum: " + startdatum);
        }
    }

}
