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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HsaInfoCoordinate {

    @JsonProperty("typ") private String typ;
    @JsonProperty("x") private String x;
    @JsonProperty("y") private String y;

    //Default constructor required by json mapper
    private HsaInfoCoordinate() {
    }

    public HsaInfoCoordinate(String typ, String x, String y) {
        this.typ = typ;
        this.x = x;
        this.y = y;
    }

    public String getTyp() {
        return typ;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return typ == null && x == null && y == null;
    }

}
