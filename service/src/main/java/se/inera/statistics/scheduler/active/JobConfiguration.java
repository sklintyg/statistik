/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

import java.time.Duration;

/**
 * Jobs depends on external redis for locking, and the same instance as for caching (see infra) is used.
 */
@Profile("caching-enabled")
@Configuration
@EnableAsync
@EnableScheduling
public class JobConfiguration {

    private static final int POOL_SIZE = 5;
    private static final int LOCK_AT_MOST_MINUTES = 20;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private MessageLogConsumer messageLogConsumer;

    @Bean
    public ScheduledLockConfiguration taskScheduler(LockProvider lockProvider) {
        return ScheduledLockConfigurationBuilder
                .withLockProvider(lockProvider)
                .withPoolSize(POOL_SIZE)
                .withDefaultLockAtMostFor(Duration.ofMinutes(LOCK_AT_MOST_MINUTES))
                .build();
    }

    @Bean
    public LockProvider lockProvider() {
        return new RedisLockProvider(jedisConnectionFactory, "statistik");
    }

    @Bean
    public MessageJob messageJob() {
        return new MessageJob(messageLogConsumer);
    }

    @Bean
    public LogJob logJob() {
        return new LogJob();
    }

    @Bean
    public ReceiveHistoryJob receiveHistoryJob() {
        return new ReceiveHistoryJob();
    }
}
