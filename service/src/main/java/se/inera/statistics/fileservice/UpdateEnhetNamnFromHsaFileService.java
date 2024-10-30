/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static jakarta.xml.bind.JAXB.unmarshal;
import static se.inera.intyg.statistik.logging.MdcLogConstants.SPAN_ID_KEY;
import static se.inera.intyg.statistik.logging.MdcLogConstants.TRACE_ID_KEY;

import java.io.InputStream;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.statistik.logging.MdcCloseableMap;
import se.inera.intyg.statistik.logging.MdcHelper;
import se.inera.intyg.statistik.logging.MdcLogConstants;
import se.inera.intyg.statistik.logging.PerformanceLogging;
import se.inera.statistics.service.processlog.EnhetManager;

public class UpdateEnhetNamnFromHsaFileService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateEnhetNamnFromHsaFileService.class);

    private static final String JOB_NAME = "FileserviceJob.run";

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

    private final EnhetManager enhetManager;
    private final MdcHelper mdcHelper;

    public UpdateEnhetNamnFromHsaFileService(EnhetManager enhetManager, MdcHelper mdcHelper) {
        this.enhetManager = enhetManager;
        this.mdcHelper = mdcHelper;
    }

    @Scheduled(cron = "${scheduler.fileservice.cron}")
    @SchedulerLock(name = JOB_NAME)
    @PrometheusTimeMethod(help = "Jobb för att uppdatera enhetsnamn från HSAs fileservice")
    @PerformanceLogging(eventAction = "update-unit-names-fropm-hsa-file-job", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
    public void doUpdateEnhetnamesFromHsaFileService() {
        try (MdcCloseableMap mdc =
            MdcCloseableMap.builder()
                .put(TRACE_ID_KEY, mdcHelper.traceId())
                .put(SPAN_ID_KEY, mdcHelper.spanId())
                .build()
        ) {
            LOG.info("Starting UpdateEnhetNamnFromHsaFileService");
            final long start = System.currentTimeMillis();
            LOG.info("Fetching unit names from HSA fileservice");
            InputStream units = HsaUnitSource.getUnits(hsaCertificateFile, hsaCertificatePassword,
                hsaTruststoreFile, hsaTruststorePassword, hsaunitsUrl);
            doUpdateEnhetnamesFromHsaFileServiceInputstream(units);
            final long end = System.currentTimeMillis();
            LOG.info("Updated from fileservice in " + String.valueOf(end - start) + " milliseconds");
        }
    }

    void doUpdateEnhetnamesFromHsaFileServiceInputstream(InputStream inputStream) {
        final long start = System.currentTimeMillis();
        final FileGetHsaUnitsResponse resp = unmarshalXml(inputStream);

        LOG.info("Updating enhet names");
        int updateCount = enhetManager.updateName(resp.getHsaUnits().getHsaUnit());

        final int enheterCount = resp.getHsaUnits().getHsaUnit().size();
        final long end = System.currentTimeMillis();
        final String totTime = String.valueOf(end - start);
        LOG.info(updateCount + " enheter uppdated of " + enheterCount + " enheter in " + totTime + " milliseconds");
    }

    static FileGetHsaUnitsResponse unmarshalXml(InputStream inputStream) {
        return unmarshal(inputStream, FileGetHsaUnitsResponse.class);
    }

}