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

public class SimpleKonResponse<T extends SimpleKonDataRow> {

    private final List<T> rows;
    private final int numberOfMonthsCalculated;

    public SimpleKonResponse(List<T> rows, int numberOfMonthsCalculated) {
        this.rows = rows;
        this.numberOfMonthsCalculated = numberOfMonthsCalculated;
    }

    public List<T> getRows() {
        return rows;
    }

    public int getNumberOfMonthsCalculated() {
        return numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (SimpleKonDataRow row : rows) {
            groups.add(row.getName());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Kon sex) {
        List<Integer> data = new ArrayList<>();
        for (SimpleKonDataRow row : rows) {
            data.add(row.getDataForSex(sex));
        }
        return data;
    }

    public List<Integer> getSummedData() {
        List<Integer> data = new ArrayList<>();
        for (SimpleKonDataRow row : rows) {
            data.add(row.getFemale() + row.getMale());
        }
        return data;
    }

    @Override
    public String toString() {
        return "{\"SimpleKonResponse\":{" + "\"rows\":" + rows + ", \"numberOfMonthsCalculated\":" + getNumberOfMonthsCalculated() + "}}";
    }

    public static SimpleKonResponse<SimpleKonDataRow> create(KonDataResponse konDataResponse, int numberOfMonthsCalculated) {
        if (konDataResponse == null) {
            return new SimpleKonResponse<>(Collections.<SimpleKonDataRow>emptyList(), numberOfMonthsCalculated);
        }
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        for (int i = 0; i < konDataResponse.getGroups().size(); i++) {
            simpleKonDataRows.add(createRowFromDataIndex(konDataResponse, i));
        }
        return new SimpleKonResponse<>(simpleKonDataRows, numberOfMonthsCalculated);
    }

    private static SimpleKonDataRow createRowFromDataIndex(KonDataResponse diagnosgruppResponse, int index) {
        int sumFemale = 0;
        int sumMale = 0;
        for (KonDataRow konDataRow : diagnosgruppResponse.getRows()) {
            final KonField konField = konDataRow.getData().get(index);
            sumFemale += konField.getFemale();
            sumMale += konField.getMale();
        }
        final String groupName = diagnosgruppResponse.getGroups().get(index);
        return new SimpleKonDataRow(groupName, sumFemale, sumMale);
    }

}
