/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.hsa.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author rlindsjo
 */
public class Vardenhet implements Serializable {

    private String id;
    private String namn;
    private String vardgivarId;
    private String vardgivarNamn;

    public Vardenhet() {
    }

    public Vardenhet(String id, String namn, String vardgivarId) {
        this(id, namn, vardgivarId, vardgivarId);
    }

    public Vardenhet(String id, String namn, String vardgivarId, String vardgivarNamn) {
        this.id = id;
        this.namn = namn;
        this.vardgivarId = vardgivarId;
        this.vardgivarNamn = vardgivarNamn;
    }

    public String getNamn() {
        return namn;
    }

    public String getId() {
        return id == null ? null : id.toUpperCase(Locale.ENGLISH);
    }

    public String getVardgivarId() {
        return vardgivarId == null ? null : vardgivarId.toUpperCase(Locale.ENGLISH);
    }

    public String getVardgivarNamn() {
        return vardgivarNamn;
    }

    @Override
    public String toString() {
        return "Vardenhet " + id + " " + namn + " " + vardgivarId;
    }
}
