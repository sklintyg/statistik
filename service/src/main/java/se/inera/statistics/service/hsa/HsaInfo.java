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
package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class HsaInfo {

    @JsonProperty("enhet")
    private HsaInfoEnhet enhet;
    @JsonProperty("huvudenhet")
    private HsaInfoEnhet huvudenhet;
    @JsonProperty("vardgivare")
    private HsaInfoVg vardgivare;
    @JsonProperty("personal")
    private HsaInfoPersonal personal;

    //Default constructor required by json mapper
    HsaInfo() {
    }

    public HsaInfo(HsaInfoEnhet enhet, HsaInfoEnhet huvudenhet, HsaInfoVg vardgivare, HsaInfoPersonal personal) {
        this.enhet = enhet;
        this.huvudenhet = huvudenhet;
        this.vardgivare = vardgivare;
        this.personal = personal;
    }

    public HsaInfoEnhet getEnhet() {
        return enhet;
    }

    public HsaInfoEnhet getHuvudenhet() {
        return huvudenhet;
    }

    public HsaInfoVg getVardgivare() {
        return vardgivare;
    }

    public HsaInfoPersonal getPersonal() {
        return personal;
    }

    public boolean hasEnhet() {
        return enhet != null;
    }

    public boolean hasHuvudenhet() {
        return huvudenhet != null;
    }

    public boolean hasVardgivare() {
        return vardgivare != null;
    }

    public boolean hasPersonal() {
        return personal != null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("enhet", enhet)
            .add("huvudenhet", huvudenhet)
            .add("vardgivare", vardgivare)
            .add("personal", personal)
            .toString();
    }

}
