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
package se.inera.statistics.web.model;

import java.util.Collections;
import java.util.List;

public class LoginInfo {

    private final String hsaId;
    private final String name;
    private final Verksamhet defaultVerksamhet;
    private final boolean loggedIn;
    private final List<Verksamhet> businesses;
    private boolean verksamhetschef;
    private boolean delprocessledare;
    private boolean processledare;

    public LoginInfo() {
        loggedIn = false;
        hsaId = "";
        name = "";
        defaultVerksamhet = null;
        businesses = Collections.emptyList();
        verksamhetschef = false;
        delprocessledare = false;
        processledare = false;
    }

    public LoginInfo(String hsaId, String name, Verksamhet defaultVerksamhet, boolean verksamhetschef, boolean delprocessledare, boolean processledare, List<Verksamhet> businesses) {
        this.hsaId = hsaId;
        this.name = name;
        this.defaultVerksamhet = defaultVerksamhet;
        this.verksamhetschef = verksamhetschef;
        this.delprocessledare = delprocessledare;
        this.processledare = processledare;
        this.loggedIn = true;
        this.businesses = businesses;
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public Verksamhet getDefaultVerksamhet() {
        return defaultVerksamhet;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public List<Verksamhet> getBusinesses() {
        return businesses;
    }

    public boolean isVerksamhetschef() {
        return verksamhetschef;
    }

    public boolean isDelprocessledare() {
        return delprocessledare;
    }

    public boolean isProcessledare() {
        return processledare;
    }

}
