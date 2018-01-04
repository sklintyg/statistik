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
package se.inera.statistics.hsa.model;

import java.util.Collections;
import java.util.List;

public class GetStatisticsPersonResponseDto {

    private String hsaIdentity;
    private String age;
    private String gender;
    private List<String> paTitleCodes = Collections.emptyList();
    private List<String> hsaTitles = Collections.emptyList();
    private List<String> specialityCodes = Collections.emptyList();
    private Boolean protectedPerson;

    public String getHsaIdentity() {
        return hsaIdentity;
    }

    public void setHsaIdentity(String hsaIdentity) {
        this.hsaIdentity = hsaIdentity;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getPaTitleCodes() {
        return paTitleCodes;
    }

    public void setPaTitleCodes(List<String> paTitleCodes) {
        this.paTitleCodes = paTitleCodes;
    }

    public List<String> getHsaTitles() {
        return hsaTitles;
    }

    public void setHsaTitles(List<String> hsaTitles) {
        this.hsaTitles = hsaTitles;
    }

    public List<String> getSpecialityCodes() {
        return specialityCodes;
    }

    public void setSpecialityCodes(List<String> specialityCodes) {
        this.specialityCodes = specialityCodes;
    }

    public void setProtectedPerson(Boolean protectedPerson) {
        this.protectedPerson = protectedPerson;
    }

    public Boolean isProtectedPerson() {
        return protectedPerson;
    }
}
