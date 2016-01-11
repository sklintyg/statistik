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
package se.inera.statistics.hsa.stub;

import java.util.ArrayList;
import java.util.List;

import se.inera.statistics.hsa.model.Vardenhet;

/**
 * @author rlindsjo
 */
public class HsaServiceStub {

    // Data cache

    private List<Vardenhet> vardenheter = new ArrayList<>();
    private List<Medarbetaruppdrag> medarbetaruppdrag = new ArrayList<>();

    public Vardenhet getVardenhet(String hsaIdentity) {

        for (Vardenhet vardenhet : vardenheter) {
            if (vardenhet.getId().equals(hsaIdentity)) {
                return vardenhet;
            }
        }
        return null;
    }

    public List<Vardenhet> getVardenhets() {
        return vardenheter;
    }

    public void deleteEnhet(String id) {
        for (Vardenhet enhet: vardenheter) {
            if (enhet.getId().equals(id)) {
                vardenheter.remove(enhet);
            }
        }
    }

    public List<Medarbetaruppdrag> getMedarbetaruppdrag() {
        return medarbetaruppdrag;
    }
}
