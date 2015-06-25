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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DiagnosgruppResponse extends KonDataResponse {

    private final List<? extends Icd> icdTyps;

    public DiagnosgruppResponse(List<? extends Icd> icdTyps, List<KonDataRow> rows) {
        super(getStringGroups(icdTyps), rows);
        this.icdTyps = icdTyps;
    }

    public List<? extends Icd> getIcdTyps() {
        return icdTyps;
    }

    private static List<String> getStringGroups(List<? extends Icd> icdTyps) {
        if (icdTyps == null) {
            return new ArrayList<>();
        }
        return Lists.transform(icdTyps, new Function<Icd, String>() {
            @Override
            public String apply(Icd icd) {
                return icd.asString();
            }
        });
    }

    public List<String> getDiagnosisGroupsAsStrings() {
        return getStringGroups(icdTyps);
    }

    @Override
    public String toString() {
        return "{\"DiagnosgruppResponse\":{" + "\"icdTyps\":" + icdTyps + ", \"rows\":" + getRows() + "}}";
    }

}
