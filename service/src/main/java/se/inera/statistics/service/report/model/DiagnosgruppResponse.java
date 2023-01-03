/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiagnosgruppResponse extends KonDataResponse {

    private final List<? extends Icd> icdTyps;

    public DiagnosgruppResponse(AvailableFilters availableFilters, List<? extends Icd> icdTyps, List<KonDataRow> rows) {
        super(availableFilters, getStringGroups(icdTyps), rows);
        this.icdTyps = icdTyps;
    }

    public List<Icd> getIcdTyps() {
        return icdTyps.stream().map(t -> (Icd) t).collect(Collectors.toList());
    }

    private static List<String> getStringGroups(List<? extends Icd> icdTyps) {
        if (icdTyps == null) {
            return new ArrayList<>();
        }
        return icdTyps.stream().map(Icd::asString).collect(Collectors.toList());
    }

    public List<String> getDiagnosisGroupsAsStrings() {
        return getStringGroups(icdTyps);
    }

    @Override
    public String toString() {
        return "{\"DiagnosgruppResponse\":{" + "\"icdTyps\":" + icdTyps + ", \"rows\":" + getRows() + "}}";
    }

}
