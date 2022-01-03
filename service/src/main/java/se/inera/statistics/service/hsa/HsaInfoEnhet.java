/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Immutable value class.
 */
public class HsaInfoEnhet {

    @JsonProperty("id")
    private String id;
    @JsonProperty("enhetsTyp")
    private List<String> enhetsTyp;
    @JsonProperty("agarform")
    private List<String> agarform;
    @JsonProperty("startdatum")
    private LocalDateTime startdatum;
    @JsonProperty("slutdatum")
    private LocalDateTime slutdatum;
    @JsonProperty("arkiverad")
    private Boolean arkiverad;
    @JsonProperty("verksamhet")
    private List<String> verksamhet;
    @JsonProperty("vardform")
    private List<String> vardform;
    @JsonProperty("geografi")
    private HsaInfoEnhetGeo geografi;
    @JsonProperty("vgid")
    private String vgid;

    // Default constructor required by json mapper
    private HsaInfoEnhet() {
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Parameter number check ignored in Sonar
    public HsaInfoEnhet(String id, List<String> enhetsTyp, List<String> agarform, LocalDateTime startdatum, LocalDateTime slutdatum,
        Boolean arkiverad, List<String> verksamhet, List<String> vardform, HsaInfoEnhetGeo geografi, String vgid) {
        this.id = id;
        this.enhetsTyp = enhetsTyp;
        this.agarform = agarform;
        this.startdatum = startdatum;
        this.slutdatum = slutdatum;
        this.arkiverad = arkiverad;
        this.verksamhet = verksamhet;
        this.vardform = vardform;
        this.geografi = geografi;
        this.vgid = vgid;
    }
    // CHECKSTYLE:ON ParameterNumberCheck

    public String getId() {
        return id;
    }

    public List<String> getEnhetsTyp() {
        if (enhetsTyp == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(enhetsTyp);
    }

    public List<String> getAgarform() {
        if (agarform == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(agarform);
    }

    public LocalDateTime getStartdatum() {
        return startdatum;
    }

    public LocalDateTime getSlutdatum() {
        return slutdatum;
    }

    public Boolean isArkiverad() {
        return arkiverad;
    }

    public List<String> getVerksamhet() {
        if (verksamhet == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(verksamhet);
    }

    public List<String> getVardform() {
        if (vardform == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(vardform);
    }

    public HsaInfoEnhetGeo getGeografi() {
        return geografi;
    }

    public String getVgid() {
        return vgid;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("enhetsTyp", enhetsTyp)
            .add("agarform", agarform)
            .add("startdatum", startdatum)
            .add("slutdatum", slutdatum)
            .add("arkiverad", arkiverad)
            .add("verksamhet", verksamhet)
            .add("vardform", vardform)
            .add("geografi", geografi)
            .add("vgid", vgid)
            .toString();
    }

}
