package se.inera.statistics.web.model;

import java.util.List;

public class TableData {

    private List<TableRow> rows;
    private List<String> headers;

    public TableData(List<TableRow> rows, List<String> headers) {
        super();
        this.rows = rows;
        this.headers = headers;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

}
