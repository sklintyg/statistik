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
