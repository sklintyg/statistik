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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.inera.intyg.infra.integration.ia.services.IABannerService;
import se.inera.intyg.infra.monitoring.logging.LogMDCHelper;

/**
 * Jobs depends on external redis for locking, and the same instance as for caching (see infra) is used.
 */
@Profile("caching-enabled")
@Configuration
@EnableScheduling
public class BannerJobConfiguration {

    @Autowired
    private IABannerService iaBannerService;

    @Autowired
    private LogMDCHelper logMDCHelper;

    @Bean
    public BannerJob bannerJob() {
        return new BannerJob(iaBannerService, logMDCHelper);
    }
}
