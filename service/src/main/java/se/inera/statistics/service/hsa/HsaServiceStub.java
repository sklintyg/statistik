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
package se.inera.statistics.service.hsa;

import java.util.ArrayList;
import java.util.List;
import se.inera.statistics.hsa.model.Vardenhet;
import se.inera.statistics.hsa.services.Medarbetaruppdrag;

/**
 * @author rlindsjo
 */
public class HsaServiceStub {

    // Data cache

    private List<Vardenhet> vardenheter = new ArrayList<>();
    private List<Medarbetaruppdrag> medarbetaruppdrag = new ArrayList<>();

    public Vardenhet getVardenhet(String hsaIdentity) {

        for (Vardenhet vardenhet : vardenheter) {
            if (vardenhet.getId().getId().equals(hsaIdentity)) {
                return vardenhet;
            }
        }
        return null;
    }

    public List<Vardenhet> getVardenhets() {
        return vardenheter;
    }

    public void deleteEnhet(String id) {
        vardenheter.removeIf(vardenhet -> vardenhet.getId().getId().equals(id));
    }

    public List<Medarbetaruppdrag> getMedarbetaruppdrag() {
        return medarbetaruppdrag;
    }
}
