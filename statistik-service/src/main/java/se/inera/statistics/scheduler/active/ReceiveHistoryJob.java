/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.scheduler.active;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import se.inera.statistics.service.processlog.Receiver;

@Component
public class ReceiveHistoryJob {
    private static final int DELAY_MS = 300_000;

    public static final int HISTORY_ITEMS = 5;

    @Autowired
    private Receiver receiver;

    private History history = new History(HISTORY_ITEMS);

    @Scheduled(fixedDelay = DELAY_MS)
    public void checkReceived() {
        insert(receiver.getAccepted());
    }

    private synchronized void insert(long accepted) {
        synchronized (history) {
            history.add(accepted);
        }
    }

    public long getCurrentRate() {
        synchronized (history) {
            return history.getCurrent() - history.getOldest();
        }
    }

    private static class History {

        private final long[] jobHistory;
        private int index;

        History(int size) {
            jobHistory = new long[size];
        }

        public void add(long value) {
            index++;
            index %= jobHistory.length;
            jobHistory[index] = value;
        }

        public long getCurrent() {
            return jobHistory[index];
        }

        public long getOldest() {
            return jobHistory[(index + 1) % jobHistory.length ];
        }
    }
}
