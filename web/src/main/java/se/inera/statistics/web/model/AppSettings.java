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
package se.inera.statistics.web.model;

import java.util.List;
import se.inera.statistics.web.error.Message;

public class AppSettings {

    private String loginUrl;
    private boolean loginVisible;
    private boolean isLoggedIn;
    private String projectVersion;
    private List<Message> driftbanners;
    private int simultaneousCallsAllowed;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public boolean isLoginVisible() {
        return loginVisible;
    }

    public void setLoginVisible(boolean loginVisible) {
        this.loginVisible = loginVisible;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setDriftbanners(List<Message> driftbanners) {
        this.driftbanners = driftbanners;
    }

    public List<Message> getDriftbanners() {
        return driftbanners;
    }

    public void setSimultaneousCallsAllowed(int simultaneousCallsAllowed) {
        this.simultaneousCallsAllowed = simultaneousCallsAllowed;
    }

    public int getSimultaneousCallsAllowed() {
        return simultaneousCallsAllowed;
    }
}
