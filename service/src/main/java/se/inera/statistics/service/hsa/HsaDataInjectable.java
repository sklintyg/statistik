/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.util.List;
import se.inera.statistics.hsa.model.HsaIdLakare;

public interface HsaDataInjectable {

    void addPersonal(HsaIdLakare id, String firstName, String lastName, HsaKon kon, int age, List<String> befattning, boolean skyddad);

    void setCountyForNextIntyg(String countyCode);

    void setKommunForNextIntyg(String kommunCode);

    void setHuvudenhetIdForNextIntyg(String huvudenhetId);

    void setEnhetNameForNextIntyg(String name);

    void setHsaKey(HSAKey hsaKey);

}
