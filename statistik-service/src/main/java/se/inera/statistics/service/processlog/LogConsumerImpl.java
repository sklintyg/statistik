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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import se.inera.ifv.statistics.spi.authorization.impl.HsaCommunicationException;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.schemavalidation.SchemaValidator;
import se.inera.statistics.service.schemavalidation.ValidateXmlResponse;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v2.RegisterCertificateType;

@Component
public class LogConsumerImpl implements LogConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(LogConsumerImpl.class);
    public static final int BATCH_SIZE = 100;

    @Autowired
    private ProcessLog processLog;

    @Autowired
    private Processor processor;

    @Autowired
    private HSADecorator hsa;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    @Autowired
    private SchemaValidator schemaValidator;

    private volatile boolean isRunning = false;

    @Transactional
    @Override
    public synchronized int processBatch() {
        try {
            setRunning(true);
            List<IntygEvent> result = processLog.getPending(BATCH_SIZE);
            if (result.isEmpty()) {
                return 0;
            }
            int processed = 0;
            for (IntygEvent event: result) {
                final IntygFormat format = event.getFormat();
                try {
                    final boolean eventSuccessfullyHandled = handleEvent(event, format);
                    if (!eventSuccessfullyHandled) {
                        LOG.error("Failed to process intyg {} ({})", event.getId(), event.getCorrelationId());
                    }
                } catch (HsaCommunicationException e) {
                    LOG.error("Could not process intyg {} ({}). {}", event.getId(), event.getCorrelationId(), e.getMessage());
                    return processed;
                } catch (Exception e) {
                    LOG.error("Could not process intyg {} ({}). {}", event.getId(), event.getCorrelationId(), e.getMessage());
                } finally {
                    processLog.confirm(event.getId());
                    processed++;
                    LOG.info("Processed log id {}", event.getId());
                }
            }
            return processed;
        }
        finally {
            setRunning(false);
        }
        }


    private boolean handleEvent(IntygEvent event, IntygFormat format) {
        switch (format) {
            case REGISTER_MEDICAL_CERTIFICATE:
                final boolean successfullyProcessedJson = processJsonMedicalCertificate(event);
                if (!successfullyProcessedJson) {
                    return false;
                }
                break;
            case REGISTER_CERTIFICATE:
                final boolean successfullyProcessedRc = processRegisterCertificate(event);
                if (!successfullyProcessedRc) {
                    return false;
                }
                break;
            default:
                LOG.warn("Unhandled intyg format: " + format);
                return false;
        }
        return true;
    }

    private boolean processRegisterCertificate(IntygEvent event) {
        try {
            final RegisterCertificateType rc = registerCertificateHelper.unmarshalRegisterCertificateXml(event.getData());
            final String intygTyp = rc.getIntyg().getTyp().getCode().toUpperCase().trim();
            final ValidateXmlResponse validation = schemaValidator.validate(intygTyp, event.getData());
            if (!validation.isValid()) {
                LOG.warn("Register certificate validation failed: " + validation.getValidationErrors());
                return false;
            }
            final boolean successfullyProcessedXml = processXmlCertificate(event, rc);
            if (!successfullyProcessedXml) {
                return false;
            }
        } catch (Exception e) {
            LOG.warn("Failed to unmarshal intyg xml");
            LOG.debug("Failed to unmarshal intyg xml", e);
            return false;
        }
        return true;
    }

    /**
     * @return true for success, otherwise false
     */
    private boolean processJsonMedicalCertificate(IntygEvent event) {
        EventType type = event.getType();
        JsonNode intyg = JSONParser.parse(event.getData());
        HsaInfo hsaInfo = hsa.decorate(intyg, event.getCorrelationId());
        if (hsaInfo != null || type.equals(EventType.REVOKED)) {
            processor.accept(intyg, hsaInfo, event.getId(), event.getCorrelationId(), type);
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return true for success, otherwise false
     */
    private boolean processXmlCertificate(IntygEvent event, RegisterCertificateType rc) {
        EventType type = event.getType();
        HsaInfo hsaInfo = hsa.populateHsaData(rc, event.getCorrelationId());
        if (hsaInfo == null && !type.equals(EventType.REVOKED)) {
            return false;
        }
        processor.accept(rc, hsaInfo, event.getId(), event.getCorrelationId(), type);
        return true;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    private synchronized void setRunning(boolean b) {
        isRunning = b;
    }

}
