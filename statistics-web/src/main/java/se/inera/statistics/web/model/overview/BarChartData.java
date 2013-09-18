package se.inera.statistics.web.model.overview;

public class BarChartData {

    private final String name;
    private final int quantity;

    public BarChartData(String name, int quantity) {
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
