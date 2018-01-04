/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.testsupport;

import se.inera.statistics.service.hsa.HsaKon;

import java.util.List;

public class Personal {

    private String id;
    private String firstName;
    private String lastName;
    private HsaKon kon;
    private int age;
    private List<String> befattning;
    private boolean skyddad;

    public Personal(String id, String firstName, String lastName, HsaKon kon, int age, List<String> befattning, boolean skyddad) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.kon = kon;
        this.age = age;
        this.befattning = befattning;
        this.skyddad = skyddad;
    }

    /**
     * For json mapper.
     */
    Personal() { }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public HsaKon getKon() {
        return kon;
    }

    public int getAge() {
        return age;
    }

    public List<String> getBefattning() {
        return befattning;
    }

    public boolean isSkyddad() {
        return skyddad;
    }

}
