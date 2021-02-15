/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.integration.hsa.model;

import java.util.List;

/**
 * @author andreaskaltenbach
 */
public class Medarbetaruppdrag {

    public static final String STATISTIK = "Statistik";
    public static final String VARD_OCH_BEHANDLING = "Vård och behandling";

    private HsaIdUser hsaId;
    private List<HsaIdEnhet> enhetIds;

    private String andamal = STATISTIK;

    Medarbetaruppdrag() {
        //Not sure why/if this is needed
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
