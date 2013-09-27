package se.inera.statistics.web.model;

import java.util.Collections;
import java.util.List;

public class TableData {

    private final List<NamedData> rows;
    private final List<String> headers;

    public TableData(List<NamedData> rows, List<String> headers) {
        this.rows = rows;
        this.headers = Collections.unmodifiableList(headers);
    }

    public List<NamedData> getRows() {
        return rows;
    }

    public List<String> getHeaders() {
        return headers;
    }

}
