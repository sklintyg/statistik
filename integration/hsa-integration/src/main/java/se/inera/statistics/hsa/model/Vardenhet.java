/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.hsa.model;

import java.io.Serializable;

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
        return id;
    }

    public String getVardgivarId() {
        return vardgivarId;
    }

    public String getVardgivarNamn() {
        return vardgivarNamn;
    }

    @Override
    public String toString() {
        return "Vardenhet " + id + " " + namn + " " + vardgivarId;
    }
}
