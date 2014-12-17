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

public class Avsnitt implements Comparable<Avsnitt>, ICDTyp {

    private final String id;
    private final String name;
    private final String firstId;
    private final String lastId;
    private final int numericalId;

    private final List<Kategori> kategoriList = new ArrayList<>();

    public Avsnitt(String id, String name) {
        this.id = id;
        this.name = name;
        String[] split = id.split("-");
        this.firstId = split[0];
        this.lastId = split[1];
        this.numericalId = -1;
    }

    public Avsnitt(String id, String name, int numericalId, List<Kategori> kategoriList) {
        this.id = id;
        this.name = name;
        String[] split = id.split("-");
        this.firstId = split[0];
        this.lastId = split[1];
        this.numericalId = numericalId;
        this.kategoriList.addAll(kategoriList);
    }

    @Override
    public String getName() {
        return asString();
    }

    @Override
    public int getNumericalId() {
        return numericalId;
    }

    public List<Kategori> getKategoris() {
        return kategoriList;
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

    public static Avsnitt fromIcd10Avsnitt(Icd10.Avsnitt source) {
        List<Kategori> kategoris = Lists.transform(source.getKategori(), new Function<Icd10.Kategori, Kategori>() {
            @Override
            public Kategori apply(Icd10.Kategori sourceKategori) {
                return Kategori.fromIcd10Kategori(sourceKategori);
            }
        });
        return new Avsnitt(source.getId(), source.getName(), source.toInt(), kategoris);
    }

}
