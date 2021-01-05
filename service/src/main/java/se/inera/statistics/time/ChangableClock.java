/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.time;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * This clock is used as a substitute to JodaTime's DateTimeUtils.setCurrent*** which is used
 * in testing to get the system to a known date.
 * It should therefore be injected and included in all calls to JavaTime.now() methods.
 */
public final class ChangableClock extends Clock implements Serializable {

    private static final long serialVersionUID = 1;

    private Clock currentClock;
    private final boolean changable;

    private ChangableClock(Clock currentClock, boolean changable) {
        Objects.requireNonNull(currentClock, "currentClock");
        this.currentClock = currentClock;
        this.changable = changable;
    }

    /**
     * To be used when creating a clock instance that should be possible to change.
     */
    public static ChangableClock newChangable() {
        return new ChangableClock(Clock.system(ZoneId.systemDefault()), true);
    }

    /**
     * Change the underlying clock.
     *
     * @param currentClock The new underlying clock
     */
    public void setCurrentClock(Clock currentClock) {
        if (!changable) {
            throw new IllegalStateException("ChangableClock is not changable");
        }
        this.currentClock = currentClock;
    }

    @Override
    public ZoneId getZone() {
        return currentClock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return currentClock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return currentClock.instant();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ChangableClock that = (ChangableClock) o;
        return com.google.common.base.Objects.equal(currentClock, that.currentClock);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), currentClock);
    }

    @Override
    public String toString() {
        return "ChangableClock:" + currentClock.toString();
    }

}
