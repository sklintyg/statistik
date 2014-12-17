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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import se.inera.statistics.service.report.util.Icd10;

import java.util.ArrayList;
import java.util.List;

public class Kapitel implements Comparable<Kapitel>, ICDTyp {

    private final String id;
    private final String name;
    private final String firstId;
    private final String lastId;
    private final int numericalId;

    private final List<Avsnitt> avsnittList = new ArrayList<>();

    public Kapitel(String id, String name, int numericalId, List<Avsnitt> avsnittList) {
        this.id = id;
        this.name = name;
        this.numericalId = numericalId;
        String[] split = id.split("-");
        firstId = split[0];
        lastId = split[1];
        this.avsnittList.addAll(avsnittList);
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
    public String getName() {
        return asString();
    }

    @Override
    public int getNumericalId() {
        return numericalId;
    }

    public List<Avsnitt> getAvsnitts() {
        return avsnittList;
    }

    @Override
    public String toString() {
        return "{\"Kapitel\":{" + "\"id\":\"" + id + '"' + ", \"name\":\"" + name + '"' + ", \"firstId\":\"" + firstId + '"' + ", \"lastId\":\"" + lastId + '"' + "}}";
    }

    @Override
    public int compareTo(Kapitel o) {
        return id.compareTo(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Kapitel) {
            return isEqual((Kapitel) obj);
        }
        return false;
    }

    private boolean isEqual(Kapitel other) {
        return id.equals(other.id);
    }

    public static Kapitel fromIcd10Kapitel(Icd10.Kapitel source) {
        List<Avsnitt> avsnitt = Lists.transform(source.getAvsnitt(), new Function<Icd10.Avsnitt, Avsnitt>() {
            @Override
            public Avsnitt apply(Icd10.Avsnitt sourceAvsnitt) {
                return Avsnitt.fromIcd10Avsnitt(sourceAvsnitt);
            }
        });
        return new Kapitel(source.getId(), source.getName(), source.toInt(), avsnitt);
    }

}
