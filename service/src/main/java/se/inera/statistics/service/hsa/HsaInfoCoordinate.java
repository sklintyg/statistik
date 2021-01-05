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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class HsaInfoCoordinate {

    @JsonProperty("typ")
    private String typ;
    @JsonProperty("x")
    private String x;
    @JsonProperty("y")
    private String y;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("typ", typ)
            .add("x", x)
            .add("y", y)
            .toString();
    }

}
