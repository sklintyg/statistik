/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.auth.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import se.inera.statistics.integration.hsa.model.HsaIdUser;
import se.inera.statistics.integration.hsa.model.HsaIdVardgivare;
import se.inera.statistics.integration.hsa.model.Vardenhet;
import se.inera.statistics.integration.hsa.model.Vardgivare;

public class User implements Serializable {

    private final HsaIdUser hsaId;
    private final String name;
    private final List<Vardgivare> vgsWithProcessledarStatus;
    private final List<Vardenhet> vardenhetList;

    public User(HsaIdUser hsaId, String name, List<Vardgivare> vgsWithProcessledarStatus, List<Vardenhet> vardenhetsList) {
        this.hsaId = hsaId;
        this.name = name;
        this.vgsWithProcessledarStatus = vgsWithProcessledarStatus != null ? Collections.unmodifiableList(vgsWithProcessledarStatus)
            : Collections.emptyList();
        this.vardenhetList = vardenhetsList != null ? Collections.unmodifiableList(vardenhetsList) : Collections.emptyList();
    }

    public boolean isProcessledareForVg(HsaIdVardgivare vardgivareId) {
        if (vardgivareId == null) {
            return false;
        }
        return vgsWithProcessledarStatus.stream().anyMatch(vg -> vg.getId().equalsIgnoreCase(vardgivareId.getId()));
    }

    public HsaIdUser getHsaId() {
        return hsaId;
    }

    public String getName() {
        return name;
    }

    public List<Vardenhet> getVardenhetList() {
        return vardenhetList;
    }

    public UserAccessLevel getUserAccessLevelForVg(HsaIdVardgivare vg) {
        return new UserAccessLevel(isProcessledareForVg(vg), getVardenhetsForVg(vg).size());
    }

    public List<Vardgivare> getVgsWithProcessledarStatus() {
        return vgsWithProcessledarStatus;
    }

    public List<Vardenhet> getVardenhetsForVg(HsaIdVardgivare vardgivare) {
        return vardenhetList.stream()
            .filter(vardenhet -> vardenhet.getVardgivarId().equals(vardgivare))
            .collect(Collectors.toList());
    }

}
