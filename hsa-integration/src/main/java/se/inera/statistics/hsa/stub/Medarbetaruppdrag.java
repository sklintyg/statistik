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

import java.util.List;

/**
 * @author andreaskaltenbach
 */
public class Medarbetaruppdrag {

    public static final String STATISTIK = "Statistik";

    private String hsaId;
    private List<String> enhetIds;

    private String andamal = STATISTIK;

    public Medarbetaruppdrag() {
    }

    public Medarbetaruppdrag(String hsaId, List<String> enhetIds) {
        this(hsaId, enhetIds, STATISTIK);
    }

    public Medarbetaruppdrag(String hsaId, List<String> enhetIds, String andamal) {
        this.hsaId = hsaId;
        this.enhetIds = enhetIds;
        this.andamal = andamal;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public List<String> getEnhetIds() {
        return enhetIds;
    }

    public void setEnhetIds(List<String> enhetIds) {
        this.enhetIds = enhetIds;
    }

    public String getAndamal() {
        return andamal;
    }

    public void setAndamal(String andamal) {
        this.andamal = andamal;
    }
}
