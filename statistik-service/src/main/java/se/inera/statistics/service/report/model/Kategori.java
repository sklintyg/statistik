/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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

public class Kategori implements Comparable<Kategori>, ICDTyp {

    private final String id;
    private final String name;
    private final int numericalId;

    public Kategori(String id, String name) {
        this.id = id;
        this.name = name;
        this.numericalId = -1;
    }

    public Kategori(String id, String name, int numericalId) {
        this.id = id;
        this.name = name;
        this.numericalId = numericalId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return asString();
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
    public int getNumericalId() {
        return numericalId;
    }

    @Override
    public String toString() {
        return "{\"Avsnitt\":{" + "\"id\":\"" + id + '"' + ", \"name\":\"" + name + '"' + "}}";
    }

    @Override
    public int compareTo(Kategori o) {
        return id.compareTo(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Kategori) {
            return isEqual((Kategori) obj);
        }
        return false;
    }

    private boolean isEqual(Kategori other) {
        return id.equals(other.id);
    }

    public static Kategori fromIcd10Kategori(Icd10.Kategori source) {
        return new Kategori(source.getId(), source.getName(), source.toInt());
    }

}
