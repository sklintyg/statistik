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

package se.inera.statistics.web.service;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import se.inera.statistics.web.model.NamedData;
import se.inera.statistics.web.model.TableData;
import se.inera.statistics.web.model.TableHeader;

public class CsvConverter {

    private final StringBuilder csv = new StringBuilder();

    CsvConverter(TableData tableData) {
        convert(tableData);
    }

    private String getCsv() {
        return csv.toString();
    }

    private void convert(TableData tableData) {
        for (List<TableHeader> list : tableData.getHeaders()) {
            for (TableHeader tableHeader : list) {
                for (int i = 0; i < tableHeader.getColspan(); i++) {
                    addField(tableHeader.getText());
                }
            }
            newRow();
        }
        for (NamedData namedData : tableData.getRows()) {
            addField(namedData.getName());
            for (Object value : namedData.getData()) {
                addField(value);
            }
            newRow();
        }
    }

    private void newRow() {
        csv.append("\n");
    }

    private void addField(Object value) {
        csv.append(value.toString()).append(';');
    }

    static Response getCsvResponse(final TableData tableData, final String fileName) {
        ResponseBuilder response = Response.ok((Object) new CsvConverter(tableData).getCsv());
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        return response.build();
    }

}
