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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.warehouse.WidelineManager;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v2.RegisterCertificateType;

public class Processor {
    @Autowired
    private WidelineManager widelineManager;

    @Autowired
    private VardgivareManager vardgivareManager;

    @Autowired
    private LakareManager lakareManager;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    public void accept(JsonNode utlatande, JsonNode hsa, long logId, String correlationId, EventType type) {
        final String enhetId = DocumentHelper.getEnhetId(utlatande, DocumentHelper.getIntygVersion(utlatande));
        saveEnhetAndLakare(hsa, enhetId);

        final Patientdata patientData = DocumentHelper.getPatientData(utlatande);
        widelineManager.accept(utlatande, patientData, hsa, logId, correlationId, type);
    }

    public void accept(RegisterCertificateType utlatande, JsonNode hsa, long logId, String correlationId, EventType type) {
        final String enhetId = registerCertificateHelper.getEnhetId(utlatande);
        saveEnhetAndLakare(hsa, enhetId);

        final Patientdata patientData = registerCertificateHelper.getPatientData(utlatande);
        widelineManager.accept(utlatande, patientData, hsa, logId, correlationId, type);
    }

    private void saveEnhetAndLakare(JsonNode hsa, String enhetId) {
        vardgivareManager.saveEnhet(hsa, enhetId);
        lakareManager.saveLakare(hsa);
    }

}
