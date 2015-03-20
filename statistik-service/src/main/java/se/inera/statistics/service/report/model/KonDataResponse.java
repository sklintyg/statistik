/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public abstract class KonDataResponse {

    private final List<String> groups;
    private final List<KonDataRow> rows;

    public KonDataResponse(List<String> groups, List<KonDataRow> rows) {
        this.groups = groups;
        this.rows = rows;
    }

    public List<String> getGroups() {
        return groups;
    }

    public List<KonDataRow> getRows() {
        return rows;
    }

    public List<String> getPeriods() {
        List<String> periods = new ArrayList<>();
        for (KonDataRow row : rows) {
            periods.add(row.getName());
        }
        return periods;
    }

    public List<Integer> getDataFromIndex(int index, Kon sex) {
        List<Integer> indexData = new ArrayList<>();
        for (KonDataRow row : rows) {
            List<KonField> data = row.getData();
            indexData.add(data.get(index).getValue(sex));
        }
        return indexData;
    }

    @Override
    public String toString() {
        return "{\"KonDataResponse\":{" + "\"groups\":" + groups + ", \"rows\":" + rows + "}}";
    }

}
