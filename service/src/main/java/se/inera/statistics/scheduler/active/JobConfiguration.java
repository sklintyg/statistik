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

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.inera.intyg.statistik.logging.MdcHelper;
import se.inera.statistics.fileservice.UpdateEnhetNamnFromHsaFileService;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.EnhetManager;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.processlog.intygsent.IntygsentLogConsumer;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

/**
 * Jobs depends on external redis for locking, and the same instance as for caching (see infra) is used.
 */
@Configuration
@EnableScheduling
@Profile("caching-enabled")
@ComponentScan({"se.inera.intyg.statistik"})
@EnableSchedulerLock(defaultLockAtMostFor = "PT20M")
public class JobConfiguration {

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private MessageLogConsumer messageLogConsumer;

    @Autowired
    private LogConsumer consumer;

    @Autowired
    private IntygsentLogConsumer intygsentLogConsumer;

    @Autowired
    private MdcHelper mdcHelper;

    @Autowired
    @Qualifier("serviceMonitoringLogService")
    private MonitoringLogService monitoringLogService;

    @Autowired
    private EnhetManager enhetManager;

    @Bean
    public LockProvider lockProvider() {
        return new RedisLockProvider(jedisConnectionFactory, "statistik");
    }

    @Bean
    public LogJob logJob() {
        return new LogJob(monitoringLogService, consumer, intygsentLogConsumer, messageLogConsumer, mdcHelper);
    }

    @Bean
    public UpdateEnhetNamnFromHsaFileService updateEnhetNamnFromHsaFileService() {
        return new UpdateEnhetNamnFromHsaFileService(enhetManager, mdcHelper);
    }
}