/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SimpleKonResponses {

    private SimpleKonResponses() {
    }

    public static SimpleKonResponse addExtrasToNameDuplicates(SimpleKonResponse input) {
        final List<SimpleKonDataRow> rows = input.getRows();
        final Map<Object, String> namePerExtras = getNamePerExtrasWhereExtrasIsAddedToDuplicates(rows);
        final List<SimpleKonDataRow> updatedRows = new ArrayList<>(rows.size());
        for (SimpleKonDataRow row : rows) {
            updatedRows.add(new SimpleKonDataRow(namePerExtras.get(row.getExtras()), row.getData(), row.getExtras()));
        }
        return new SimpleKonResponse(input.getAvailableFilters(), updatedRows);
    }

    private static Map<Object, String> getNamePerExtrasWhereExtrasIsAddedToDuplicates(List<SimpleKonDataRow> rows) {
        Multimap<CaseInsensiviteString, SimpleKonDataRow> m = HashMultimap.create();
        for (SimpleKonDataRow row : rows) {
            final CaseInsensiviteString caseInsensiviteName = new CaseInsensiviteString(row.getName());
            m.put(caseInsensiviteName, row);
        }
        final HashMap<Object, String> map = new HashMap<>();
        for (Map.Entry<CaseInsensiviteString, Collection<SimpleKonDataRow>> entry : m.asMap().entrySet()) {
            for (SimpleKonDataRow row : entry.getValue()) {
                final String name = entry.getValue().size() > 1 ? row.getName() + " " + row.getExtras() : row.getName();
                map.put(row.getExtras(), name);
            }
        }
        return map;
    }

}
