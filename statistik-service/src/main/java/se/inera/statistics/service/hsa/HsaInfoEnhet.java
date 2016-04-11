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
package se.inera.statistics.service.hsa;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable value class.
 */
public class HsaInfoEnhet {

    @JsonProperty("id") private String id;
    @JsonProperty("enhetsTyp") private List<String> enhetsTyp;
    @JsonProperty("agarform") private List<String> agarform;
    @JsonProperty("startdatum") private LocalDateTime startdatum;
    @JsonProperty("slutdatum") private LocalDateTime slutdatum;
    @JsonProperty("arkiverad") private Boolean arkiverad;
    @JsonProperty("verksamhet") private List<String> verksamhet;
    @JsonProperty("vardform") private List<String> vardform;
    @JsonProperty("geografi") private HsaInfoEnhetGeo geografi;
    @JsonProperty("vgid") private String vgid;

    //Default constructor required by json mapper
    private HsaInfoEnhet() {
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    public HsaInfoEnhet(String id, List<String> enhetsTyp, List<String> agarform, LocalDateTime startdatum, LocalDateTime slutdatum, Boolean arkiverad, List<String> verksamhet, List<String> vardform, HsaInfoEnhetGeo geografi, String vgid) {
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
        return new ArrayList<>(enhetsTyp);
    }

    public List<String> getAgarform() {
        return new ArrayList<>(agarform);
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
        return new ArrayList<>(verksamhet);
    }

    public List<String> getVardform() {
        return new ArrayList<>(vardform);
    }

    public HsaInfoEnhetGeo getGeografi() {
        return geografi;
    }

    public String getVgid() {
        return vgid;
    }

}
