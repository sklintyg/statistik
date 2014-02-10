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

package se.inera.statistics.service.sjukfall;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.LocalDate;

@Entity
@NamedQueries(value = { @NamedQuery(name = "SjukfallByPersonIdAndVardgivareId", query = "SELECT s FROM Sjukfall s WHERE s.personId = :personId AND s.vardgivareId = :vardgivareId ORDER BY s.start DESC ") })
@Table(name = "sjukfall")
public class Sjukfall {

    @Id
    private String id;

    private String personId;
    private String vardgivareId;
    private String start;
    private String end;

    /**
     * Empty constructor (as required by JPA spec).
     */
    public Sjukfall() {
    }

    public Sjukfall(String personId, String vardgivareId, LocalDate start, LocalDate end) {
        id = UUID.randomUUID().toString();
        this.personId = personId;
        this.vardgivareId = vardgivareId;
        this.start = start.toString();
        this.end = end.toString();
    }

    public String getPersonId() {
        return personId;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public LocalDate getStart() {
        return new LocalDate(start);
    }

    public LocalDate getEnd() {
        return new LocalDate(end);
    }

    public void setEnd(LocalDate end) {
        this.end = end.toString();
    }

    public String getId() {
        return id;
    }
}
