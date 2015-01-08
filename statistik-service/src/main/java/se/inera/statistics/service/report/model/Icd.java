/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

import se.inera.statistics.service.report.util.Icd10;

public class Icd implements Comparable<Icd>, ICDTyp {

    private final String id;
    private final String name;
    private final int numericalId;

    public Icd(String id, String name) {
        this.id = id;
        this.name = name;
        this.numericalId = -1;
    }

    public Icd(String id, String name, int numericalId) {
        this.id = id;
        this.name = name;
        this.numericalId = numericalId;
    }

    public Icd(Icd10.Id source) {
        this(source.getId(), source.getName(), source.toInt());
    }

    @Override
    public String getName() {
        return asString();
    }

    @Override
    public int getNumericalId() {
        return numericalId;
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
        return "{\"Icd\":{" + "\"id\":\"" + id + '"' + ", \"name\":\"" + name + '"' + "}}";
    }

    @Override
    public int compareTo(Icd o) {
        return id.compareTo(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Icd) {
            return isEqual((Icd) obj);
        }
        return false;
    }

    private boolean isEqual(Icd other) {
        return id.equals(other.id);
    }

}
