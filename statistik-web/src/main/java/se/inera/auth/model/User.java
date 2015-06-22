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
package se.inera.auth.model;

import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.Vardenhet;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class User implements Serializable {

    private final HsaIdUser hsaId;
    private final String name;
    private final boolean processledare;
    private final Vardenhet vardenhet;
    private final List<Vardenhet> vardenhetList;

    public User(HsaIdUser hsaId, String name, boolean processledare, Vardenhet vardenhet, List<Vardenhet> vardenhetsList) {
        this.hsaId = hsaId;
        this.name = name;
        this.processledare = processledare;
        this.vardenhet = vardenhet;
        this.vardenhetList = Collections.unmodifiableList(vardenhetsList);
    }

    public HsaIdUser getHsaId() {
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
        return !processledare && vardenhetList.size() > 1;
    }

    public boolean isProcessledare() {
        return processledare;
    }

}
