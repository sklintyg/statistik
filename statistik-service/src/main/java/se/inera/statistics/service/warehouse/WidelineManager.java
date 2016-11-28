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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.RegisterCertificateHelper;
import se.inera.statistics.service.hsa.HsaInfo;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.warehouse.model.db.WideLine;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@Component
public class WidelineManager {

    private static final Logger LOG = LoggerFactory.getLogger(WidelineManager.class);

    private static int errCount;

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private WidelineConverter widelineConverter;

    @Autowired
    private RegisterCertificateHelper registerCertificateHelper;

    @Transactional(noRollbackFor = Exception.class)
    public void accept(JsonNode intyg, Patientdata patientdata, HsaInfo hsa, long logId, String correlationId, EventType type) {
        String intygid = DocumentHelper.getIntygId(intyg, DocumentHelper.getIntygVersion(intyg));
        if (!isSupportedIntygType(DocumentHelper.getIntygType(intyg))) {
            LOG.info("Intygtype not supported. Ignoring intyg: " + intygid);
            return;
        }
        for (WideLine line : widelineConverter.toWideline(intyg, patientdata, hsa, logId, correlationId, type)) {
            persistIfValid(logId, intygid, line);
        }
    }

    private void persistIfValid(long logId, String intygid, WideLine line) {
        List<String> errors = widelineConverter.validate(line);

        if (errors.isEmpty()) {
            manager.persist(line);
        } else {
            StringBuilder errorBuilder = new StringBuilder("Faulty intyg logid ").append(logId).append(" id ").append(intygid).append(" error count ").append(errCount++);
            for (String error : errors) {
                errorBuilder.append('\n').append(error);
            }
            LOG.error(errorBuilder.toString());
        }
    }

    @Transactional(noRollbackFor = Exception.class)
    public void accept(RegisterCertificateType intyg, Patientdata patientData, HsaInfo hsa, long logId, String correlationId, EventType type) {
        final String intygid = registerCertificateHelper.getIntygId(intyg);
        final String intygtyp = registerCertificateHelper.getIntygtyp(intyg);
        if (!isSupportedIntygType(intygtyp)) {
            LOG.info("Intygtype not supported. Ignoring intyg: " + intygid);
            return;
        }
        for (WideLine line : widelineConverter.toWideline(intyg, patientData, hsa, logId, correlationId, type)) {
            persistIfValid(logId, intygid, line);
        }
    }

    private boolean isSupportedIntygType(String intygType) {
        for (IntygType type : IntygType.values()) {
            if (type.name().equalsIgnoreCase(intygType)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void saveWideline(WideLine line) {
        manager.persist(line);
    }

    @Transactional
    public int count() {
        return ((Long) manager.createQuery("SELECT COUNT (wl) FROM WideLine wl").getSingleResult()).intValue();
    }

}
