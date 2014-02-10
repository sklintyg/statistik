/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.report.repository.NationellUpdater;

@Component
public class NationellUpdaterJob {
    private static final Logger LOG = LoggerFactory.getLogger(NationellUpdaterJob.class);

    @Autowired
    private NationellUpdater updater;

    @Scheduled(cron = "${scheduler.nationellJob.cron}")
    public void checkLog() {
        LOG.info("Nationell Job");
        updater.updateAldersgrupp();
        updater.updateCasesPerMonth();
        updater.updateDiagnosgrupp();
        updater.updateDiagnosundergrupp();
        updater.updateSjukfallslangd();
        updater.updateSjukskrivningsgrad();
    }
}
