/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import se.inera.statistics.hsa.model.HsaIdUser;
import se.inera.statistics.hsa.model.HsaIdVardgivare;
import se.inera.statistics.hsa.model.Vardenhet;

public class User implements Serializable {

    private final HsaIdUser hsaId;
    private final String name;
    private final List<HsaIdVardgivare> vgsWithProcessledarStatus;
    private final List<Vardenhet> vardenhetList;
    private HsaIdVardgivare selectedVardgivare;
    private UserAccessLevel userAccessLevel;

    public User(HsaIdUser hsaId, String name, List<HsaIdVardgivare> vgsWithProcessledarStatus, HsaIdVardgivare selectedVardgivare, List<Vardenhet> vardenhetsList) {
        this.hsaId = hsaId;
        this.name = name;
        this.vgsWithProcessledarStatus = vgsWithProcessledarStatus != null ? Collections.unmodifiableList(vgsWithProcessledarStatus) : Collections.emptyList();
        this.vardenhetList = vardenhetsList != null ? Collections.unmodifiableList(vardenhetsList) : Collections.emptyList();
        setSelectedVardgivare(selectedVardgivare);
    }

    private boolean isProcessledareForSelectedVg() {
        return isProcessledareForVg(this.selectedVardgivare);
    }

    public boolean isProcessledareForVg(HsaIdVardgivare vardgivareId) {
        if (vardgivareId == null) {
            return false;
        }
        return vgsWithProcessledarStatus.contains(vardgivareId);
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

    public boolean isVerksamhetschef() {
        return userAccessLevel.isVerksamhetschef();
    }

    public boolean isDelprocessledare() {
        return userAccessLevel.isDelprocessledare();
    }

    public boolean isProcessledare() {
        return userAccessLevel.isProcessledare();
    }

    public HsaIdVardgivare getSelectedVardgivare() {
        return selectedVardgivare;
    }

    public void setSelectedVardgivare(HsaIdVardgivare selectedVardgivare) {
        this.selectedVardgivare = selectedVardgivare;
        this.userAccessLevel = new UserAccessLevel(isProcessledareForSelectedVg(), getEnhetsForSelectedVardgivare().size());
    }

    public List<HsaIdVardgivare> getVgsWithProcessledarStatus() {
        return vgsWithProcessledarStatus;
    }

    private List<Vardenhet> getEnhetsForSelectedVardgivare() {
        return getVardenhetsForVg(this.selectedVardgivare);
    }

    public List<Vardenhet> getVardenhetsForVg(HsaIdVardgivare vardgivare) {
        return vardenhetList.stream()
                .filter(vardenhet -> {
                    return vardenhet.getVardgivarId().equals(vardgivare);
                })
                .collect(Collectors.toList());
    }

}
