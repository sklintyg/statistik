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
package se.inera.statistics.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.statistics.service.report.model.Icd;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.web.model.TableDataReport;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class ResponseHandler {

    public static final String ALL_AVAILABLE_DXS_SELECTED_IN_FILTER = "allAvailableDxsSelectedInFilter";

    @Autowired
    private Icd10 icd10;

    Response getResponse(TableDataReport result, String csv) {
        if (csv == null || csv.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked") Map<String, Object> mappedResult = result != null ? mapper.convertValue(result, Map.class) : Maps.newHashMap();
            final boolean allAvailableDxsSelectedInFilter = result != null ? areAllAvailableDxsSelectedInFilter(result.getFilter()) : true;
            mappedResult.put(ALL_AVAILABLE_DXS_SELECTED_IN_FILTER, allAvailableDxsSelectedInFilter);
            return Response.ok(mappedResult).build();
        }
        return CsvConverter.getCsvResponse(result.getTableData(), "export.csv");
    }

    private boolean areAllAvailableDxsSelectedInFilter(FilterDataResponse filter) {
        if (filter == null) {
            return true;
        }
        final List<String> diagnoser = filter.getDiagnoser();
        if (diagnoser == null) {
            return true;
        }
        final List<String> dxFilter = Lists.newArrayList(diagnoser);
        final List<String> topLevelIcdCodes = Lists.newArrayList(Lists.transform(icd10.getIcdStructure(), new Function<Icd, String>() {
            @Override
            public String apply(Icd icd) {
                return String.valueOf(icd.getNumericalId());
            }
        }));
        dxFilter.sort(String.CASE_INSENSITIVE_ORDER);
        topLevelIcdCodes.sort(String.CASE_INSENSITIVE_ORDER);

        return topLevelIcdCodes.equals(dxFilter);
    }

}
