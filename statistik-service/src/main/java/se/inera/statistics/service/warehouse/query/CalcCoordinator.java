/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CalcCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(CalcCoordinator.class);
    private static final int EXECUTORS = Runtime.getRuntime().availableProcessors();
    private static final int NO_OF_TICKETS = Runtime.getRuntime().availableProcessors() + 2;
    private static final Object LOCK = new Object();
    private static final long POLL_TIME = 100L;
    private static final int MAX_WAIT = 5_000;
    private static final Ticket[] TICKETS = new Ticket[NO_OF_TICKETS];
    private static int queueSize;
    static {
        for (int i = 0; i < TICKETS.length; i++) {
            TICKETS[i] = new Ticket();
        }
        LOG.info("Using " +  NO_OF_TICKETS + " tickets.");
    }

    private static boolean denyAll = false;

    private CalcCoordinator() {
    }

    public static Ticket getTicket() {
        Ticket returnTicket = null;
        int size = 0;
        synchronized (LOCK) {
            for (Ticket ticket : TICKETS) {
                if (ticket.free) {
                    ticket.free = false;
                    returnTicket = ticket;
                    size = queueSize;
                    break;
                }
            }
        }
        if (returnTicket == null || denyAll) {
            LOG.warn("No available executors");
            throw new CalcException("No available executors");
        }
        int counter = 0;
        long start = System.currentTimeMillis();
        while (size > EXECUTORS) {
            synchronized (LOCK) {
                size = queueSize;
            }
            LOG.info("Waited for " + counter++ + " loops");
            if (System.currentTimeMillis() - start > MAX_WAIT) {
                returnTicket(returnTicket);
                LOG.warn("Max wait time exceeded");
                throw new CalcException("Max wait time exceeded");
            }
            try {
                Thread.sleep(POLL_TIME);
            } catch (InterruptedException e) {
                LOG.trace("Ignoring wake-up");
            }
        }
        synchronized (LOCK) {
            queueSize++;
        }
        return returnTicket;
    }

    public static void returnTicket(Ticket ticket) {
        synchronized (LOCK) {
            if (ticket != null) {
                ticket.free = true;
                queueSize--;
            }
        }
    }

    public static void setDenyAll(boolean denyAll) {
        LOG.info("Deny all = " + denyAll);
        CalcCoordinator.denyAll = denyAll;
    }

    public static int getWorkloadPercentage() {
        final int percentageConstant = 100;
        return percentageConstant * queueSize / NO_OF_TICKETS;
    }

    public static final class Ticket {
        private boolean free = true;
    }
}
