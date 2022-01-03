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
package se.inera.statistics.service.warehouse;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.processlog.Enhet;

public final class EnhetUtil {

    private EnhetUtil() {
    }

    public static Collection<HsaIdEnhet> getAllEnheterInVardenheter(Collection<HsaIdEnhet> enheter, Warehouse warehouse) {
        if (enheter == null || enheter.isEmpty()) {
            return enheter;
        }

        return Stream.concat(enheter.stream(),
            warehouse.getEnhetsWithHsaId(enheter).stream()
                .filter(Enhet::isVardenhet)
                .flatMap(enhet -> warehouse.getEnhetsOfVardenhet(enhet.getEnhetId()).stream())
                .map(Enhet::getEnhetId))
            .collect(Collectors.toSet());
    }
}
