/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.hsa.model;

import java.io.Serializable;

/**
 * @author rlindsjo
 */
public class Vardenhet implements Serializable {

    private HsaIdEnhet id;
    private String namn;
    private HsaIdVardgivare vardgivarId;
    private String vardgivarNamn;

    Vardenhet() {
        //Not sure if/why this is needed
    }

    public Vardenhet(HsaIdEnhet id, String namn, HsaIdVardgivare vardgivarId) {
        this(id, namn, vardgivarId, vardgivarId.getId());
    }

    public Vardenhet(HsaIdEnhet id, String namn, HsaIdVardgivare vardgivarId, String vardgivarNamn) {
        this.id = id;
        this.namn = namn;
        this.vardgivarId = vardgivarId;
        this.vardgivarNamn = vardgivarNamn;
    }

    public String getNamn() {
        return namn;
    }

    public HsaIdEnhet getId() {
        return id;
    }

    public HsaIdVardgivare getVardgivarId() {
        return vardgivarId;
    }

    public String getVardgivarNamn() {
        return vardgivarNamn;
    }

    @Override
    public String toString() {
        return "Vardenhet " + id + " " + namn + " " + vardgivarId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vardenhet)) {
            return false;
        }

        Vardenhet vardenhet = (Vardenhet) o;

        return id.equals(vardenhet.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
