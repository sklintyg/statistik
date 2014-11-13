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

package se.inera.statistics.service.report.model;

public class Avsnitt implements Comparable<Avsnitt>, ICDTyp {

    private final String id;
    private final String name;
    private final String firstId;
    private final String lastId;

    public Avsnitt(String id, String name) {
        this.id = id;
        this.name = name;
        String[] split = id.split("-");
        firstId = split[0];
        lastId = split[1];
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String asString() {
        if (id.charAt(0) <= 'Z') {
            return id + " " + name;
        } else {
            return name;
        }
    }

    @Override
    public String toString() {
        return "{\"Avsnitt\":{" + "\"id\":\"" + id + '"' + ", \"name\":\"" + name + '"' + ", \"firstId\":\"" + firstId + '"' + ", \"lastId\":\"" + lastId + '"' + "}}";
    }

    @Override
    public int compareTo(Avsnitt o) {
        return id.compareTo(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Avsnitt) {
            return isEqual((Avsnitt) obj);
        }
        return false;
    }

    private boolean isEqual(Avsnitt other) {
        return id.equals(other.id);
    }

}
