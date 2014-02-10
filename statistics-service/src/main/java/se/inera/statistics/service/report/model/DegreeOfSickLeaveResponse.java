/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.model;

import java.util.ArrayList;
import java.util.List;

public class DegreeOfSickLeaveResponse {

    private final List<String> degreesOfSickLeave;
    private final List<DualSexDataRow> rows;

    public DegreeOfSickLeaveResponse(List<String> degreesOfSickLeave, List<DualSexDataRow> rows) {
        this.degreesOfSickLeave = degreesOfSickLeave;
        this.rows = rows;
    }

    public List<String> getDegreesOfSickLeave() {
        return degreesOfSickLeave;
    }

    public List<DualSexDataRow> getRows() {
        return rows;
    }

    public List<String> getPeriods() {
        List<String> periods = new ArrayList<>();
        for (DualSexDataRow row : rows) {
            periods.add(row.getName());
        }
        return periods;
    }

    public List<Integer> getDataFromIndex(int index, Sex sex) {
        List<Integer> indexData = new ArrayList<>();
        for (DualSexDataRow row : rows) {
            List<DualSexField> data = row.getData();
            indexData.add(data.get(index).getValue(sex));
        }
        return indexData;
    }

    @Override
    public String toString() {
        return "{\"DegreeOfSickLeaveResponse\":{\"degreesOfSickLeave\":" + formattedDegreesOfSickLeave() + ", \"rows\":" + rows + "}}";
    }

    private String formattedDegreesOfSickLeave() {
        StringBuilder sb = new StringBuilder("[");
        for (String s : degreesOfSickLeave) {
            sb.append('"');
            sb.append(s);
            sb.append("\", ");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 2);
        }
        sb.append(']');
        return sb.toString();
    }
}
