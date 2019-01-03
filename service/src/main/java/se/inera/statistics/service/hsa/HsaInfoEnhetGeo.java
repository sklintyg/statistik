/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class HsaInfoEnhetGeo {

    @JsonProperty("koordinat")
    private HsaInfoCoordinate koordinat;
    @JsonProperty("plats")
    private String plats;
    @JsonProperty("kommundelskod")
    private String kommundelskod;
    @JsonProperty("kommundelsnamn")
    private String kommundelsnamn;
    @JsonProperty("kommun")
    private String kommun;
    @JsonProperty("lan")
    private String lan;

    // Default constructor required by json mapper
    private HsaInfoEnhetGeo() {
    }

    public HsaInfoEnhetGeo(HsaInfoCoordinate koordinat, String plats, String kommundelskod, String kommundelsnamn, String kommun,
            String lan) {
        this.koordinat = koordinat;
        this.plats = plats;
        this.kommundelskod = kommundelskod;
        this.kommundelsnamn = kommundelsnamn;
        this.kommun = kommun;
        this.lan = lan;
    }

    public HsaInfoCoordinate getKoordinat() {
        return koordinat;
    }

    public String getPlats() {
        return plats;
    }

    public String getKommundelskod() {
        return kommundelskod;
    }

    public String getKommundelsnamn() {
        return kommundelsnamn;
    }

    public String getKommun() {
        return kommun;
    }

    public String getLan() {
        return lan;
    }

    @JsonIgnore
    @java.lang.SuppressWarnings("squid:S1067") // Expression complexity check ignored in Sonar
    public boolean isEmpty() {
        return koordinat == null && plats == null && kommundelskod == null && kommundelsnamn == null && kommun == null && lan == null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("koordinat", koordinat)
                .add("plats", plats)
                .add("kommundelskod", kommundelskod)
                .add("kommundelsnamn", kommundelsnamn)
                .add("kommun", kommun)
                .add("lan", lan)
                .toString();
    }

}
