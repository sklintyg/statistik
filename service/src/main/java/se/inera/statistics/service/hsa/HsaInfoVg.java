/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import java.time.LocalDateTime;

public class HsaInfoVg {

    @JsonProperty("id")
    private String id;
    @JsonProperty("orgnr")
    private String orgnr;
    @JsonProperty("startdatum")
    private LocalDateTime startdatum;
    @JsonProperty("slutdatum")
    private LocalDateTime slutdatum;
    @JsonProperty("arkiverad")
    private Boolean arkiverad;

    //Default constructor required by json mapper
    private HsaInfoVg() {
    }

    public HsaInfoVg(String id, String orgnr, LocalDateTime startdatum, LocalDateTime slutdatum, Boolean arkiverad) {
        this.id = id;
        this.orgnr = orgnr;
        this.startdatum = startdatum;
        this.slutdatum = slutdatum;
        this.arkiverad = arkiverad;
    }

    public String getId() {
        return id;
    }

    public String getOrgnr() {
        return orgnr;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("orgnr", orgnr)
            .add("startdatum", startdatum)
            .add("slutdatum", slutdatum)
            .add("arkiverad", arkiverad)
            .toString();
    }

}
