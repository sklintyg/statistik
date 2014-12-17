/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableData {

    private final List<NamedData> rows;
    private final List<List<TableHeader>> headers;

    public TableData(List<NamedData> rows, List<List<TableHeader>> headers) {
        this.rows = rows;
        this.headers = Collections.unmodifiableList(headers);
    }

    public static TableData createWithSingleHeadersRow(List<NamedData> rows, List<String> headers) {
        List<TableHeader> tableHeaders = toTableHeaderList(headers, 1);
        List<List<TableHeader>> headerRows = new ArrayList<>();
        headerRows.add(tableHeaders);
        return new TableData(rows, headerRows);
    }

    public static List<TableHeader> toTableHeaderList(List<String> headers, int span) {
        List<TableHeader> tableHeaders = new ArrayList<>();
        for (String headerName : headers) {
            tableHeaders.add(new TableHeader(headerName, span));
        }
        return tableHeaders;
    }

    public List<NamedData> getRows() {
        return rows;
    }

    public List<List<TableHeader>> getHeaders() {
        return headers;
    }

}
