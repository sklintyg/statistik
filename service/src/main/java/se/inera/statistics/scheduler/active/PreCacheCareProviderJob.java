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

package se.inera.statistics.scheduler.active;

import java.util.List;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.warehouse.Warehouse;

@Component
public class PreCacheCareProviderJob {

    private static final Logger LOG = LoggerFactory.getLogger(PreCacheCareProviderJob.class);
    private static final String JOB_NAME = "PreCacheCareProviderJob.run";
    private final Warehouse warehouse;
    private final List<String> precacheCareProviderIds;

    public PreCacheCareProviderJob(Warehouse warehouse,
        @Value("#{'${job.precache.careprovider.ids}'.split(',')}") List<String> precacheCareProviderIds) {
        this.warehouse = warehouse;
        this.precacheCareProviderIds = precacheCareProviderIds;
    }

    @Scheduled(cron = "${job.precache.careprovider.cron}")
    @SchedulerLock(name = JOB_NAME)
    public void run() {
        LOG.info("Started job to precache the following careproviders: {}", precacheCareProviderIds);

        precacheCareProviderIds.stream()
            .filter(id -> id != null && !id.isBlank())
            .forEach(id -> warehouse.get(new HsaIdVardgivare(id)));

        LOG.info("Completed job to precache the following careproviders: {}", precacheCareProviderIds);
    }
}
