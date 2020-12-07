/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import se.inera.auth.model.UserAccessLevel;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.region.RegionsVardgivareStatus;

public class LoginInfoVg {

    private final HsaIdVardgivare hsaId;
    private final String name;
    private final RegionsVardgivareStatus regionsVardgivareStatus;
    private final UserAccessLevel userAccessLevel;

    public LoginInfoVg(HsaIdVardgivare hsaId, String name, RegionsVardgivareStatus regionsVardgivareStatus,
        UserAccessLevel userAccessLevel) {
        this.hsaId = hsaId;
        this.name = name;
        this.regionsVardgivareStatus = regionsVardgivareStatus;
        this.userAccessLevel = userAccessLevel;
    }

    public static LoginInfoVg empty() {
        return new LoginInfoVg(HsaIdVardgivare.empty(), "", RegionsVardgivareStatus.NO_REGIONSVARDGIVARE,
            new UserAccessLevel(false, 0));
    }

    public HsaIdVardgivare getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public RegionsVardgivareStatus getRegionsVardgivareStatus() {
        return regionsVardgivareStatus;
    }

    public boolean isProcessledare() {
        return userAccessLevel.isProcessledare();
    }

    public boolean isVerksamhetschef() {
        return userAccessLevel.isVerksamhetschef();
    }

    public boolean isDelprocessledare() {
        return userAccessLevel.isDelprocessledare();
    }

    public boolean isRegionsvardgivare() {
        return !RegionsVardgivareStatus.NO_REGIONSVARDGIVARE.equals(regionsVardgivareStatus);
    }

    public boolean isRegionsvardgivareWithUpload() {
        return RegionsVardgivareStatus.REGIONSVARDGIVARE_WITH_UPLOAD.equals(regionsVardgivareStatus);
    }

    public boolean isRegionAdmin() {
        return isRegionsvardgivare() && isProcessledare();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoginInfoVg)) {
            return false;
        }

        LoginInfoVg that = (LoginInfoVg) o;

        return hsaId.equals(that.hsaId);
    }

    @Override
    public int hashCode() {
        return hsaId.hashCode();
    }
}
