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

package se.inera.statistics.hsa.stub;

import java.util.ArrayList;
import java.util.List;

/**
 * @author johannesc
 */
public class HsaUnitStub {

    private String vardgivarid;
    private String hsaId;
    private String email;
    private String name;

    private List<PersonStub> medarbetaruppdrag = new ArrayList<>();

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVardgivarid() {
        return vardgivarid;
    }

    public void setVardgivarid(String vardgivarid) {
        this.vardgivarid = vardgivarid;
    }

    public List<PersonStub> getMedarbetaruppdrag() {
        return medarbetaruppdrag;
    }

    public void setMedarbetaruppdrag(List<PersonStub> medarbetaruppdrag) {
        this.medarbetaruppdrag = medarbetaruppdrag;
    }

}
