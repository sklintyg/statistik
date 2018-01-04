/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleKonResponse<T extends SimpleKonDataRow> {

    private final List<T> rows;

    public SimpleKonResponse(List<T> rows) {
        this.rows = rows;
    }

    public List<T> getRows() {
        return rows;
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
        return "{\"SimpleKonResponse\":{" + "\"rows\":" + rows + "}}";
    }

    public static SimpleKonResponse<SimpleKonDataRow> create(KonDataResponse konDataResponse) {
        if (konDataResponse == null) {
            return new SimpleKonResponse<>(Collections.<SimpleKonDataRow>emptyList());
        }
        final ArrayList<SimpleKonDataRow> simpleKonDataRows = new ArrayList<>();
        for (int i = 0; i < konDataResponse.getGroups().size(); i++) {
            simpleKonDataRows.add(createRowFromDataIndex(konDataResponse, i));
        }
        return new SimpleKonResponse<>(simpleKonDataRows);
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

    public static SimpleKonResponse<SimpleKonDataRow> merge(Collection<SimpleKonResponse<SimpleKonDataRow>> resps, boolean mergeEqualRows) {
        final ArrayList<SimpleKonDataRow> rows = new ArrayList<>();
        if (mergeEqualRows) {
            Multimap<String, SimpleKonDataRow> mappedResps = LinkedHashMultimap.create();
            for (SimpleKonResponse<SimpleKonDataRow> resp : resps) {
                for (SimpleKonDataRow row : resp.getRows()) {
                    mappedResps.put(row.getName(), row);
                }
            }
            for (Collection<SimpleKonDataRow> sameRows : mappedResps.asMap().values()) {
                rows.add(mergeRows(sameRows));
            }
        } else {
            for (SimpleKonResponse<SimpleKonDataRow> resp : resps) {
                rows.addAll(resp.getRows());
            }
        }
        return new SimpleKonResponse<>(rows);
    }

    private static SimpleKonDataRow mergeRows(Collection<SimpleKonDataRow> rows) {
        if (rows.size() == 1) {
            return rows.iterator().next();
        }
        int male = 0;
        int female = 0;
        String name = "";
        Object extras = null;
        for (SimpleKonDataRow row : rows) {
            male += row.getMale();
            female += row.getFemale();
            name = row.getName();
            extras = row.getExtras();
        }
        return new SimpleKonDataRow(name, female, male, extras);
    }

}
