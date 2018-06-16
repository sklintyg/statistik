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

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import se.inera.statistics.service.processlog.Receiver;

public class ReceiveHistoryJob {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiveHistoryJob.class);

    private static final String JOB_NAME = "ReceiveHistoryJob.checkReceived";
    private static final int DELAY_MS = 300_000;

    public static final int HISTORY_ITEMS = 5;

    @Autowired
    private Receiver receiver;

    private final History history = new History(HISTORY_ITEMS);

    @Scheduled(fixedDelay = DELAY_MS)
    @SchedulerLock(name = JOB_NAME)
    public void checkReceived() {
        LOG.info(JOB_NAME);
        history.add(receiver.getAccepted());
    }

    public long getCurrentRate() {
        return history.rate();
    }

    private static class History {

        private final long[] jobHistory;
        private int index;

        History(int size) {
            jobHistory = new long[size];
        }

        void add(long value) {
            synchronized (this) {
                index++;
                index %= jobHistory.length;
                jobHistory[index] = value;
            }
        }

        long rate() {
            synchronized (this) {
                return jobHistory[index] - jobHistory[(index + 1) % jobHistory.length];
            }
        }
    }
}
