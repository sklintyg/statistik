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
package se.inera.statistics.service.processlog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name = EventPointer.TABLE)
public class EventPointer {
    public static final String TABLE = "handelsepekare";

    private static final int MAX_NAME_LENGTH = 50;

    @Id
    @Column(length = MAX_NAME_LENGTH)
    private String name;

    private long eventId;

    public EventPointer(String name, long id) {
        this.name = name;
        this.eventId = id;
    }

    /**
     * Empty constructor (as required by JPA spec).
     */
    public EventPointer() {
        //Empty constructor (as required by JPA spec).
    }

    public String getName() {
        return name;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
