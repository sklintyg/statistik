/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.auth.model;

import se.inera.statistics.hsa.model.Vardenhet;

import java.util.Collections;
import java.util.List;

public class User {

    private final String hsaId;
    private final String name;
    private final boolean vgAccess;
    private final Vardenhet vardenhet;
    private final List<Vardenhet> vardenhetList;

    public User(String hsaId, String name, boolean vgAccess, Vardenhet vardenhet, List<Vardenhet> vardenhetsList) {
        this.hsaId = hsaId;
        this.name = name;
        this.vgAccess = vgAccess;
        this.vardenhet = vardenhet;
        this.vardenhetList = Collections.unmodifiableList(vardenhetsList);
    }

    public String getHsaId() {
        return hsaId;
    }

    public Vardenhet getValdVardenhet() {
        return vardenhet;
    }

    public String getName() {
        return name;
    }

    public List<Vardenhet> getVardenhetList() {
        return vardenhetList;
    }

    public boolean isVerksamhetschef() {
        return !isDelprocessledare() && !isProcessledare();
    }

    public boolean isDelprocessledare() {
        return !vgAccess && vardenhetList.size() > 1;
    }

    public boolean isProcessledare() {
        return vgAccess;
    }

}
