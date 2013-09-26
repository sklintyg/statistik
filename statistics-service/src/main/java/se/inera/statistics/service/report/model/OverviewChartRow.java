package se.inera.statistics.service.report.model;

public class OverviewChartRow {

    private final String name;
    private final int quantity;

    public OverviewChartRow(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

}
