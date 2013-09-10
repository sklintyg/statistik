package se.inera.statistics.web.model;

import java.util.List;

public class TableRow {

    private String name;
    private List<Number> data;

    public TableRow(String name, List<Number> data) {
        super();
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Number> getData() {
        return data;
    }

    public void setData(List<Number> data) {
        this.data = data;
    }

}
