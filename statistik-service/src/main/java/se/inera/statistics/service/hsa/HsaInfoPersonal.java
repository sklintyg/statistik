/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HsaInfoPersonal {

    @JsonProperty("id")
    private String id;
    @JsonProperty("kon")
    private String kon;
    @JsonProperty("alder")
    private String alder;
    @JsonProperty("befattning")
    private List<String> befattning;
    @JsonProperty("specialitet")
    private List<String> specialitet;
    @JsonProperty("yrkesgrupp")
    private List<String> yrkesgrupp;
    @JsonProperty("skyddad")
    private Boolean skyddad;
    @JsonProperty("tilltalsnamn")
    private String tilltalsnamn;
    @JsonProperty("efternamn")
    private String efternamn;

    // Default constructor required by json mapper
    private HsaInfoPersonal() {
    }

    // CHECKSTYLE:OFF ParameterNumberCheck
    @java.lang.SuppressWarnings("squid:S00107") // Parameter number check ignored in Sonar
    public HsaInfoPersonal(String id, String kon, String alder, List<String> befattning, List<String> specialitet, List<String> yrkesgrupp,
            Boolean skyddad, String tilltalsnamn, String efternamn) {
        this.id = id;
        this.kon = kon;
        this.alder = alder;
        this.befattning = befattning;
        this.specialitet = specialitet;
        this.yrkesgrupp = yrkesgrupp;
        this.skyddad = skyddad;
        this.tilltalsnamn = tilltalsnamn;
        this.efternamn = efternamn;
    }
    // CHECKSTYLE:ON ParameterNumberCheck

    public String getId() {
        return id;
    }

    public String getKon() {
        return kon;
    }

    public String getAlder() {
        return alder;
    }

    public List<String> getBefattning() {
        if (befattning == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(befattning);
    }

    public List<String> getSpecialitet() {
        if (specialitet == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(specialitet);
    }

    public List<String> getYrkesgrupp() {
        if (yrkesgrupp == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(yrkesgrupp);
    }

    public Boolean isSkyddad() {
        return skyddad;
    }

    public String getTilltalsnamn() {
        return tilltalsnamn;
    }

    public String getEfternamn() {
        return efternamn;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("kon", kon)
                .add("alder", alder)
                .add("befattning", befattning)
                .add("specialitet", specialitet)
                .add("yrkesgrupp", yrkesgrupp)
                .add("skyddad", skyddad)
                .add("tilltalsnamn", tilltalsnamn)
                .add("efternamn", efternamn)
                .toString();
    }

}
