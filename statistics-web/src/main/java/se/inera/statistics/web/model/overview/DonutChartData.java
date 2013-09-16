package se.inera.statistics.web.model.overview;

public class DonutChartData {

    private final String name;
    private final int quantity;
    private final int alternation;

    public DonutChartData(String name, int quantity, int alternation) {
        this.name = name;
        this.quantity = quantity;
        this.alternation = alternation;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAlternation() {
        return alternation;
    }

}
