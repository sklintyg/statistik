/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import se.inera.statistics.service.report.util.Icd10;

public class Icd implements Comparable<Icd> {

    private final String id;
    private final String name;
    private final int numericalId;
    private final String info; //Extra information, e.g. for a tooltip

    private final List<Icd> subItems = new ArrayList<>();

    public Icd(String id, String name, int numericalId, String info) {
        this.id = id;
        this.name = name;
        this.numericalId = numericalId;
        this.info = info;
    }

    public Icd(String id, String name, int numericalId) {
        this(id, name, numericalId, null);
    }

    /**
     * Create Icd from Icd10.Id source.
     *
     * @param source Id object to use as source
     * @param includeAllDownToThisLevel How deep to include subitems, e.g. Avsnitt class would include both kapitel and avsnitt but not
     * kategori or kod.
     */
    public Icd(Icd10.Id source, Class includeAllDownToThisLevel) {
        this(source.getVisibleId(), source.getName(), source.toInt(), source.getInfo());
        if (includeAllDownToThisLevel != null && includeAllDownToThisLevel.isInstance(source)) {
            return;
        }
        for (Icd10.Id subItem : source.getSubItems()) {
            if (subItem.isVisible()) {
                subItems.add(new Icd(subItem, includeAllDownToThisLevel));
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getNumericalId() {
        return numericalId;
    }

    public List<Icd> getSubItems() {
        return subItems;
    }

    public String getId() {
        return id;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getInfo() {
        return info;
    }

    public String asString() {
        if (!id.isEmpty() && id.charAt(0) <= 'Z') {
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
