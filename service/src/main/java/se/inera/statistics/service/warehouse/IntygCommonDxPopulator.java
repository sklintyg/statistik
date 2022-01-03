/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.helper.JSONParser;
import se.inera.statistics.service.helper.RegisterCertificateResolver;
import se.inera.statistics.service.helper.certificate.JsonDocumentHelper;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.processlog.IntygEvent;
import se.inera.statistics.service.processlog.IntygFormat;
import se.inera.statistics.service.warehouse.model.db.IntygCommon;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

/**
 * This class has as its only purpose to populate the new dx column in table intygcommon. All rows also available
 * in wideline table will be populated directly from there using a sql query from liquibase (script 47). The
 * remaining rows will be populated from this class. It is hence only required to activate this class att the
 * first startup after running liquibase script nr 47.
 */
@Component
@Profile("populateIntygCommonDx")
public class IntygCommonDxPopulator implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(IntygCommonDxPopulator.class);

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Autowired
    private RegisterCertificateResolver registerCertificateResolver;

    @Autowired
    private IntygCommonConverter intygCommonConverter;

    @Override
    @Transactional
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent ignore) {
        LOG.info("Start populate dx in intygcommon");
        final CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        final CriteriaQuery<IntygCommon> query = criteriaBuilder.createQuery(IntygCommon.class);
        final Root<IntygCommon> from = query.from(IntygCommon.class);
        final CriteriaQuery<IntygCommon> dxIsNullQuery = query.select(from)
            .where(criteriaBuilder.isNull(from.get("dx")));
        final List<IntygCommon> resultList = manager.createQuery(dxIsNullQuery).getResultList();
        LOG.info("Found " + resultList.size() + " rows in intygcommon where dx is null which will be populated");

        final CriteriaQuery<IntygEvent> ieq = criteriaBuilder.createQuery(IntygEvent.class);
        final Root<IntygEvent> ier = ieq.from(IntygEvent.class);
        final ParameterExpression<String> corridParam = criteriaBuilder.parameter(String.class);
        final CriteriaQuery<IntygEvent> ieQuery = ieq.select(ier)
            .where(criteriaBuilder.equal(ier.get("correlationId"), corridParam));

        resultList.forEach(intygCommon -> {
            LOG.info("Processing intyg" + intygCommon.getIntygid());
            final TypedQuery<IntygEvent> intygEventQuery = manager.createQuery(ieQuery);
            intygEventQuery.setParameter(corridParam, intygCommon.getIntygid());
            final List<IntygEvent> ieResults = intygEventQuery.getResultList();
            if (ieResults != null && !ieResults.isEmpty()) {
                final IntygEvent intygEvent = ieResults.get(0);
                final String dx = getDx(intygEvent, intygEvent.getFormat());
                if (dx != null) {
                    LOG.info("Populating dx for intyg" + intygCommon.getIntygid());
                    intygCommon.setDx(dx);
                }
            }
        });
        LOG.info("Done populate dx in intygcommon");
    }

    private String getDx(IntygEvent event, IntygFormat format) {
        switch (format) {
            case REGISTER_MEDICAL_CERTIFICATE:
                return getDxJsonMedicalCertificate(event);
            case REGISTER_CERTIFICATE:
                return getDxRegisterCertificate(event);
            default:
                LOG.warn("Unhandled intyg format: " + format);
                return null;
        }
    }

    private String getDxJsonMedicalCertificate(IntygEvent event) {
        JsonNode intyg = JSONParser.parse(event.getData());
        IntygDTO dto = JsonDocumentHelper.convertToDTO(intyg);
        return dto.getDiagnoskod();
    }

    private String getDxRegisterCertificate(IntygEvent event) {
        try {
            final String data = event.getData();

            final RegisterCertificateType rc = registerCertificateResolver.unmarshalXml(data);
            IntygDTO dto = registerCertificateResolver.convertToDTO(rc);
            return intygCommonConverter.parseDiagnos(dto.getDiagnoskod());
        } catch (Exception e) {
            LOG.warn("Failed to unmarshal intyg xml");
            LOG.debug("Failed to unmarshal intyg xml", e);
            return null;
        }
    }

}
