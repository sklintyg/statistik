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
package se.inera.statistics.service.landsting;

import se.inera.statistics.hsa.model.HsaIdEnhet;

public class LandstingEnhetFileDataRow {

    private HsaIdEnhet enhetensHsaId;
    private Integer listadePatienter;

    public LandstingEnhetFileDataRow(HsaIdEnhet enhetensHsaId, Integer listadePatienter) {
        this.enhetensHsaId = enhetensHsaId;
        this.listadePatienter = listadePatienter;
    }

    public HsaIdEnhet getEnhetensHsaId() {
        return enhetensHsaId;
    }

    public Integer getListadePatienter() {
        return listadePatienter;
    }

}