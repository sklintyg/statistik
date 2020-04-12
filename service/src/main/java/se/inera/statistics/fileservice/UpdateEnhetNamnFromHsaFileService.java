/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.fileservice;

import java.io.InputStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXB;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.ifv.hsawsresponder.v3.ListGetHsaUnitsResponseType;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;

public class UpdateEnhetNamnFromHsaFileService {

    @PersistenceContext(unitName = "IneraStatisticsLog")
    private EntityManager manager;

    @Value("${hsa.ws.certificate.file}")
    private String hsaCertificateFile;

    @Value("${hsa.ws.certificate.password}")
    private String hsaCertificatePassword;

    @Value("${hsa.ws.truststore.file}")
    private String hsaTruststoreFile;

    @Value("${hsa.ws.truststore.password}")
    private String hsaTruststorePassword;

    @Value("${hsaunits.url}")
    private String hsaunitsUrl;

    private static final String JOB_NAME = "FileserviceJob.run";

    @Scheduled(cron = "${scheduler.fileservice.cron}")
    @SchedulerLock(name = JOB_NAME)
    @PrometheusTimeMethod(help = "Jobb för att uppdatera enhetsnamn från HSAs fileservice")
    public void doUpdateEnhetnamesFromHsaFileService() {
        LOG.info("Starting UpdateEnhetNamnFromHsaFileService");
        final long start = System.currentTimeMillis();
        LOG.info("Fetching unit names from HSA fileservice");
        InputStream units = HsaUnitSource.getUnits(hsaCertificateFile, hsaCertificatePassword,
            hsaTruststoreFile, hsaTruststorePassword, hsaunitsUrl);
        doUpdateEnhetnamesFromHsaFileServiceInputstream(units);
        final long end = System.currentTimeMillis();
        LOG.info("Updated from fileservice in " + String.valueOf(end - start) + " milliseconds");
    }

    void doUpdateEnhetnamesFromHsaFileServiceInputstream(InputStream inputStream) {
        final long start = System.currentTimeMillis();
        final ListGetHsaUnitsResponseType resp = unmarshalXml(inputStream);

        LOG.info("Updating enhet names");
        final Integer updateCount = resp.getHsaUnits().getHsaUnit().stream().reduce(0, (integer, hsaUnit) -> {
            final Query q = manager.createNativeQuery("update enhet set namn = :namn where enhetId = :id and namn <> :namn");
            final String name = hsaUnit.getName();
            q.setParameter("namn", name);
            final String hsaid = hsaUnit.getHsaIdentity();
            q.setParameter("id", hsaid);
            final int updated = q.executeUpdate();
            if (updated > 0) {
                LOG.info("Id: " + hsaid + ", Namn: " + name);
            }
            return updated;

        }, (integer, integer2) -> integer + integer2);

        final int enheterCount = resp.getHsaUnits().getHsaUnit().size();
        final long end = System.currentTimeMillis();
        final String totTime = String.valueOf(end - start);
        LOG.info(updateCount + " enheter uppdated of " + enheterCount + " enheter in " + totTime + " milliseconds");
    }

    static ListGetHsaUnitsResponseType unmarshalXml(InputStream inputStream) {
        return JAXB.unmarshal(inputStream, ListGetHsaUnitsResponseType.class);
    }

    private static final Logger LOG = LoggerFactory.getLogger(UpdateEnhetNamnFromHsaFileService.class);
}
