/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import se.inera.statistics.service.warehouse.WarehouseManager;

@Component
public class FactReloadJob {
    private static final Logger LOG = LoggerFactory.getLogger(FactReloadJob.class);

    @Autowired
    private WarehouseManager manager;

    @Scheduled(cron = "${scheduler.factReloadJob.cron}")
    public void checkLog() {
        LOG.info("Fact Reload Job started");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int lines = manager.loadWideLines();
        stopWatch.stop();
        LOG.info("Fact Reload Job completed. Line count: " + lines + " time: " + stopWatch.getTotalTimeMillis() + "ms");
    }

}
