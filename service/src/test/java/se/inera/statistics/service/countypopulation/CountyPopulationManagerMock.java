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
package se.inera.statistics.service.countypopulation;

import java.util.HashMap;
import java.util.Iterator;
import se.inera.statistics.service.report.model.KonField;
import se.inera.statistics.service.report.model.Lan;
import se.inera.statistics.service.report.model.Range;

public class CountyPopulationManagerMock implements CountyPopulationManager {

    @Override
    public CountyPopulation getCountyPopulation(Range range) {
        final HashMap<String, KonField> populationPerCountyCode = new HashMap<>();
        final Iterator<String> iterator = new Lan().iterator();
        iterator.forEachRemaining(s -> populationPerCountyCode.put(s, new KonField(Integer.parseInt(s) * 900, Integer.parseInt(s) * 500)));
        return new CountyPopulation(populationPerCountyCode, range.getFrom());
    }

}
