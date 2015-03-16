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

public class SjukfallslangdResponse {

    private final List<SjukfallslangdRow> sickLeaveGroupsRows;
    private final int months;

    public SjukfallslangdResponse(List<SjukfallslangdRow> rows, int numberOfMonthsCalculated) {
        this.sickLeaveGroupsRows = rows;
        this.months = numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (SjukfallslangdRow row : sickLeaveGroupsRows) {
            groups.add(row.getGroup());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Kon sex) {
        List<Integer> data = new ArrayList<>();
        for (SjukfallslangdRow row : sickLeaveGroupsRows) {
            if (sex == Kon.Female) {
                data.add(row.getFemale());
            } else {
                data.add(row.getMale());
            }
        }
        return data;
    }

    public List<SjukfallslangdRow> getRows() {
        return sickLeaveGroupsRows;
    }

    public int getMonths() {
        return months;
    }

    @Override
    public String toString() {
        return "{\"SjukfallslangdResponse\":{\"sickLeaveGroupsRows\":" + sickLeaveGroupsRows + ", \"months\":" + months + "}}";
    }
}
