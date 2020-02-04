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
package se.inera.statistics.service.warehouse.query;

import static se.inera.statistics.service.warehouse.query.OverviewQuery.PERCENT;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public final class CalcCoordinator {

    private static final Logger LOG = LoggerFactory.getLogger(CalcCoordinator.class);
    private static final int POLL_TIME = 100;

    @Value("${calcCoordinator.maxConcurrentTasks:4}")
    private int maxConcurrentTasks;
    @Value("${calcCoordinator.maxWaitingTasks:4}")
    private int maxWaitingTasks;
    @Value("${calcCoordinator.waitTimeoutMillis:15000}")
    private int maxWait;

    private AtomicInteger tasks = new AtomicInteger();
    private AtomicInteger waits = new AtomicInteger();
    private volatile boolean denyAll;

    /**
     * Allows maxConcurrentTasks (concurrent tasks), and maxWaitingTasks (waiting tasks).
     *
     * @param task the task to run
     * @param <T> return type
     * @return the task return value
     * @throws CalcException when no ticket is available.
     * @throws Exception on task errors.
     */
    public <T> T submit(Callable<T> task) throws Exception {
        if (denyAll) {
            LOG.info("No available executors, denyAll active");
            throw new CalcException("No available executors, denyAll active");
        }

        for (int n = 0; n < maxWait;) {
            try {
                if (tasks.incrementAndGet() < maxConcurrentTasks) {
                    return task.call();
                }
                n += await();
            } finally {
                tasks.decrementAndGet();
            }
        }

        LOG.warn("No available executors, max wait time exceeded");
        throw new CalcException("Max wait time exceeded");
    }

    @Profile("testapi")
    public void setDenyAll(boolean denyAll) {
        LOG.info("Deny all = " + denyAll);
        this.denyAll = denyAll;
    }

    public int getWorkloadPercentage() {
        return PERCENT * tasks.get() / maxConcurrentTasks;
    }

    private int await() {
        try {
            if (waits.incrementAndGet() >= maxWaitingTasks) {
                LOG.warn("No available executors, max queue size exceeded");
                throw new CalcException("No available executors, max queue size exceeded");
            }
            TimeUnit.MILLISECONDS.sleep(POLL_TIME);
        } catch (InterruptedException e) {
            LOG.trace("Ignoring wake-up");
        } finally {
            waits.decrementAndGet();
        }
        return POLL_TIME;
    }
}
