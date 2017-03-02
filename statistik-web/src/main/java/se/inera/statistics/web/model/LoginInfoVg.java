/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import se.inera.auth.model.UserAccessLevel;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.service.landsting.LandstingsVardgivareStatus;

public class LoginInfoVg {

    private final HsaIdVardgivare hsaId;
    private final String name;
    private final LandstingsVardgivareStatus landstingsVardgivareStatus;
    private final UserAccessLevel userAccessLevel;

    public LoginInfoVg(HsaIdVardgivare hsaId, String name, LandstingsVardgivareStatus landstingsVardgivareStatus,
            UserAccessLevel userAccessLevel) {
        this.hsaId = hsaId;
        this.name = name;
        this.landstingsVardgivareStatus = landstingsVardgivareStatus;
        this.userAccessLevel = userAccessLevel;
    }

    public static LoginInfoVg empty() {
        return new LoginInfoVg(HsaIdVardgivare.empty(), "", LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE,
                new UserAccessLevel(false, 0));
    }

    public HsaIdVardgivare getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public LandstingsVardgivareStatus getLandstingsVardgivareStatus() {
        return landstingsVardgivareStatus;
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

    public boolean isLandstingsvardgivare() {
        return !LandstingsVardgivareStatus.NO_LANDSTINGSVARDGIVARE.equals(landstingsVardgivareStatus);
    }

    public boolean isLandstingsvardgivareWithUpload() {
        return LandstingsVardgivareStatus.LANDSTINGSVARDGIVARE_WITH_UPLOAD.equals(landstingsVardgivareStatus);
    }

    public boolean isLandstingAdmin() {
        return isLandstingsvardgivare() && isProcessledare();
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
