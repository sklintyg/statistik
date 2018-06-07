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
package se.inera.statistics.service.caching;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class EmbeddedRedis {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EmbeddedRedis.class);
    private RedisServer redisServer;

    @Value("${redis.port:6379}")
    private String port;

    @PostConstruct
    public void init() {
        LOG.info("Starting embedded Redis");
        try {
            redisServer = new RedisServer(getPort());
            redisServer.start();
        } catch (Exception e) {
            LOG.warn("Failed to start embedded Redis. Is it already started?");
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.info("Stopping embedded Redis");
        try {
            redisServer.stop();
        } catch (Exception e) {
            LOG.warn("Failed to stop embedded Redis. Is it already stopped?");
        }
        while (redisServer.isActive()) {
            try {
                final long sleepTime = 100L;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOG.warn("Sleep interrupted...");
            }
        }
        LOG.info("Embedded Redis stopped");
    }

    public String getHost() {
        return "localhost";
    }

    public int getPort() {
        return Integer.valueOf(port);
    }

}
