/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intygstjanster.ts.services.RegisterTSBasResponder.v1.RegisterTSBasType;
import se.inera.intygstjanster.ts.services.RegisterTSDiabetesResponder.v1.RegisterTSDiabetesType;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.RegisterCertificateResolver;
import se.inera.statistics.service.helper.certificate.TsBasHelper;
import se.inera.statistics.service.helper.certificate.TsDiabetesHelper;
import se.inera.statistics.service.hsa.HSADecorator;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

public class Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private ProcessLog processLog;

    @Autowired
    private HSADecorator hsaDecorator;

    @Autowired
    private RegisterCertificateResolver registerCertificateResolver;

    @Autowired
    private TsBasHelper tsBasHelper;

    @Autowired
    private TsDiabetesHelper tsDiabetesHelper;

    private long accepted;

    public void accept(EventType type, String data, String documentId, long timestamp) {
        processLog.store(type, data, documentId, timestamp);
        hsa(documentId, data);
        accepted++;
    }

    public long getAccepted() {
        return accepted;
    }

    @SuppressWarnings("UnnecessaryDefaultInEnumSwitch")
    private void hsa(String documentId, String data) {
        final IntygFormat intygFormat = IntygEvent.getIntygFormat(data);
        switch (intygFormat) {
            case REGISTER_MEDICAL_CERTIFICATE:
                hsaForJson(documentId, data);
                break;
            case REGISTER_CERTIFICATE:
                hsaForXml(documentId, data);
                break;
            case REGISTER_TS_BAS:
                hsaForTsBas(documentId, data);
                break;
            case REGISTER_TS_DIABETES:
                hsaForTsDiabetes(documentId, data);
                break;
            default:
                LOG.error("Unhandled intyg format: " + intygFormat);
        }
    }

    private void hsaForJson(String documentId, String data) {
        try {
            JsonNode utlatande = JSONParser.parse(data);
            hsaDecorator.decorate(utlatande, documentId);
        } catch (Exception e) {
            LOG.error("Failed decorating json intyg {}: '{}'", documentId, e.getMessage());
            LOG.debug("Failed decorating json intyg {}", documentId, e);
        }
    }

    private void hsaForXml(String documentId, String data) {
        try {
            final RegisterCertificateType utlatande = registerCertificateResolver.unmarshalXml(data);
            hsaDecorator.populateHsaData(utlatande, documentId);
        } catch (Exception e) {
            LOG.error("Failed decorating xml intyg {}: '{}'", documentId, e.getMessage());
            LOG.error("Failed decorating stacktrace:", e);
        }
    }

    private void hsaForTsBas(String documentId, String data) {
        try {
            final RegisterTSBasType utlatande = tsBasHelper.unmarshalXml(data);
            hsaDecorator.populateHsaData(utlatande, documentId);
        } catch (Exception e) {
            LOG.error("Failed decorating xml intyg {}: '{}'", documentId, e.getMessage());
            LOG.error("Failed decorating stacktrace:", e);
        }
    }

    private void hsaForTsDiabetes(String documentId, String data) {
        try {
            final RegisterTSDiabetesType utlatande = tsDiabetesHelper.unmarshalXml(data);
            hsaDecorator.populateHsaData(utlatande, documentId);
        } catch (Exception e) {
            LOG.error("Failed decorating xml intyg {}: '{}'", documentId, e.getMessage());
            LOG.error("Failed decorating stacktrace:", e);
        }
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }

}
