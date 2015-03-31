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
import java.util.Collections;
import java.util.List;

public class KonDataResponse {

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

    public static KonDataResponse createNewWithoutEmptyGroups(List<String> groups, List<KonDataRow> rows) {
        if (groups == null || rows == null) {
            return new KonDataResponse(Collections.<String>emptyList(), Collections.<KonDataRow>emptyList());
        }
        final List<String> groupsFiltered = new ArrayList<>();
        final List<List<KonField>> rowsDataFiltered = initRowsDataFiltered(rows);

        for (int i = 0; i < groups.size(); i++) {
            if (calculateSumForIndex(rows, i) > 0) {
                groupsFiltered.add(groups.get(i));
                for (int j = 0; j < rows.size(); j++) {
                    final KonField konField = rows.get(j).getData().get(i);
                    rowsDataFiltered.get(j).add(konField);
                }
            }
        }
        final ArrayList<KonDataRow> konDataRows = createKonDataRows(rows, rowsDataFiltered);
        return new KonDataResponse(groupsFiltered, konDataRows);
    }

    private static List<List<KonField>> initRowsDataFiltered(List<KonDataRow> rows) {
        final List<List<KonField>> rowsDataFiltered = new ArrayList<>();

        for (KonDataRow row : rows) {
            rowsDataFiltered.add(new ArrayList<KonField>());
        }
        return rowsDataFiltered;
    }

    private static int calculateSumForIndex(List<KonDataRow> rows, int index) {
        int sum = 0;
        for (KonDataRow row : rows) {
            final KonField konField = row.getData().get(index);
            sum += konField.getFemale() + konField.getMale();
        }
        return sum;
    }

    private static ArrayList<KonDataRow> createKonDataRows(List<KonDataRow> rows, List<List<KonField>> rowsDataFiltered) {
        final ArrayList<KonDataRow> konDataRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            final KonDataRow row = new KonDataRow(rows.get(i).getName(), rowsDataFiltered.get(i));
            konDataRows.add(row);
        }
        return konDataRows;
    }

}
