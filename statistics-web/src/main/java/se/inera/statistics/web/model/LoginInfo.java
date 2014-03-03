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

package se.inera.statistics.web.model;

import java.util.Collections;
import java.util.List;

public class LoginInfo {

    private final String hsaId;
    private final String name;
    private final boolean isVgView;
    private final boolean isFullVgAccess;
    private final boolean loggedIn;
    private final List<Verksamhet> businesses;

    public LoginInfo() {
        this.hsaId = "";
        this.name = "";
        this.loggedIn = false;
        this.businesses = Collections.EMPTY_LIST;
        isVgView = false;
        isFullVgAccess = false;
    }

    public LoginInfo(String hsaId, String name, boolean isVgView, boolean isFullVgAccess, List<Verksamhet> businesses) {
        this.hsaId = hsaId;
        this.name = name;
        this.isVgView = isVgView;
        this.isFullVgAccess = isFullVgAccess;
        this.loggedIn = true;
        this.businesses = businesses;
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isVgView() {
        return isVgView;
    }

    public boolean isFullVgAccess() {
        return isFullVgAccess;
    }

    public List<Verksamhet> getBusinesses() {
        return businesses;
    }

}
