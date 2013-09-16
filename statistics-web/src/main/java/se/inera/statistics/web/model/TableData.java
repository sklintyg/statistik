package se.inera.statistics.web.model;

import java.util.List;

public class TableData {

    private final List<TableRow> rows;
    private final List<String> headers;

    public TableData(List<TableRow> rows, List<String> headers) {
        this.rows = rows;
        this.headers = headers;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public List<String> getHeaders() {
        return headers;
    }

}
