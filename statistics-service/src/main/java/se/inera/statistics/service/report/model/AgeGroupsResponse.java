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

import se.inera.statistics.service.report.model.db.AgeGroupsRow;

import java.util.ArrayList;
import java.util.List;

public class AgeGroupsResponse {

    private final List<AgeGroupsRow> ageGroupsRows;
    private final int months;

    public AgeGroupsResponse(List<AgeGroupsRow> rows, int numberOfMonthsCalculated) {
        this.ageGroupsRows = rows;
        this.months = numberOfMonthsCalculated;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        for (AgeGroupsRow row : ageGroupsRows) {
            groups.add(row.getGroup());
        }
        return groups;
    }

    public List<Integer> getDataForSex(Sex sex) {
        List<Integer> data = new ArrayList<>();
        for (AgeGroupsRow row : ageGroupsRows) {
            if (sex == Sex.Female) {
                data.add(row.getFemale());
            } else {
                data.add(row.getMale());
            }
        }
        return data;
    }

    public List<AgeGroupsRow> getAgeGroupsRows() {
        return ageGroupsRows;
    }

    public int getMonths() {
        return months;
    }

    @Override
    public String toString() {
        return "{\"AgeGroupsResponse\":{" + "\"ageGroupsRows\":" + ageGroupsRows + ", \"months\":" + months + "}}";
    }
}
