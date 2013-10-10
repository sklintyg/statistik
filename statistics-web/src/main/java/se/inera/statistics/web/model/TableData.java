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
        List<TableHeader> tableHeaders = new ArrayList<>();
        for (String headerName : headers) {
            tableHeaders.add(new TableHeader(headerName));
        }
        List<List<TableHeader>> headerRows = new ArrayList<>();
        headerRows.add(tableHeaders);
        return new TableData(rows, headerRows);

    }

    public List<NamedData> getRows() {
        return rows;
    }

    public List<List<TableHeader>> getHeaders() {
        return headers;
    }

}
