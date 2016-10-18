/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.hsa.services;

import se.inera.statistics.hsa.model.HsaIdEnhet;
import se.inera.statistics.hsa.model.HsaIdUser;

import java.util.List;

/**
 * @author andreaskaltenbach
 */
public class Medarbetaruppdrag {

    public static final String STATISTIK = "Statistik";

    private HsaIdUser hsaId;
    private List<HsaIdEnhet> enhetIds;

    private String andamal = STATISTIK;

    public Medarbetaruppdrag() {
    }

    public Medarbetaruppdrag(HsaIdUser hsaId, List<HsaIdEnhet> enhetIds) {
        this(hsaId, enhetIds, STATISTIK);
    }

    public Medarbetaruppdrag(HsaIdUser hsaId, List<HsaIdEnhet> enhetIds, String andamal) {
        this.hsaId = hsaId;
        this.enhetIds = enhetIds;
        this.andamal = andamal;
    }

    public HsaIdUser getHsaId() {
        return hsaId;
    }

    public void setHsaId(HsaIdUser hsaId) {
        this.hsaId = hsaId;
    }

    public List<HsaIdEnhet> getEnhetIds() {
        return enhetIds;
    }

    public void setEnhetIds(List<HsaIdEnhet> enhetIds) {
        this.enhetIds = enhetIds;
    }

    public String getAndamal() {
        return andamal;
    }

    public void setAndamal(String andamal) {
        this.andamal = andamal;
    }
}
