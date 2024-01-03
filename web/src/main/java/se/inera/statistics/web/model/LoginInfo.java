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

import com.google.common.base.Strings;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;

public class LoginInfo {

    private final HsaIdUser hsaId;
    private final String name;
    private final List<Verksamhet> businesses;
    private final List<LoginInfoVg> loginInfoVgs;
    private final UserSettingsDTO userSettings;
    private final String authenticationMethod;

    public LoginInfo() {
        hsaId = new HsaIdUser("");
        name = "";
        businesses = Collections.emptyList();
        loginInfoVgs = Collections.emptyList();
        userSettings = new UserSettingsDTO();
        authenticationMethod = "";
    }

    public LoginInfo(HsaIdUser userId, String userName, List<Verksamhet> businesses, List<LoginInfoVg> loginInfoVgs,
        UserSettingsDTO userSettings, String authenticationMethod) {
        this.hsaId = userId != null ? userId : new HsaIdUser("");
        this.name = Strings.nullToEmpty(userName);
        this.businesses = businesses != null ? Collections.unmodifiableList(businesses) : Collections.emptyList();
        this.loginInfoVgs = loginInfoVgs != null ? Collections.unmodifiableList(loginInfoVgs) : Collections.emptyList();
        this.userSettings = userSettings;
        this.authenticationMethod = authenticationMethod;
    }

    public HsaIdUser getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public boolean isLoggedIn() {
        return hsaId != null && !hsaId.isEmpty();
    }

    public Optional<LoginInfoVg> getLoginInfoForVg(HsaIdVardgivare vgId) {
        return loginInfoVgs.stream().filter(loginInfoVg -> loginInfoVg.getHsaId().equals(vgId)).findFirst();
    }

    public List<LoginInfoVg> getVgs() {
        return loginInfoVgs;
    }

    public List<Verksamhet> getBusinessesForVg(HsaIdVardgivare vgId) {
        return businesses.stream()
            .filter(verksamhet -> verksamhet.getVardgivarId().equals(vgId))
            .filter(verksamhet -> !verksamhet.getId().isEmpty())
            .collect(Collectors.toList());
    }

    public UserSettingsDTO getUserSettings() {
        return userSettings;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }
}
