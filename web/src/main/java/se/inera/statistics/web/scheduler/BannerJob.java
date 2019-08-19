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
package se.inera.statistics.web.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.intyg.infra.integration.ia.model.Application;
import se.inera.intyg.infra.integration.ia.services.IABannerService;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.infra.monitoring.logging.LogMDCHelper;

public class BannerJob {

    private static final Logger LOG = LoggerFactory.getLogger(BannerJob.class);
    private static final String JOB_NAME = "BannerJob.run";

    private IABannerService iaBannerService;
    private LogMDCHelper logMDCHelper;

    public BannerJob(IABannerService iaBannerService, LogMDCHelper logMDCHelper) {
        this.iaBannerService = iaBannerService;
        this.logMDCHelper = logMDCHelper;
    }

    @Scheduled(cron = "${intygsadmin.cron}")
    @SchedulerLock(name = JOB_NAME)
    @PrometheusTimeMethod(help = "Jobb för att hämta banners från IA")
    public void run() {
        logMDCHelper.run(() -> {
            LOG.debug(JOB_NAME);

            iaBannerService.loadBanners(Application.INTYGSSTATISTIK);
        });
    }
}
