package se.inera.statistics.web.model.overview;

public class DonutChartData {

    private String name;
    private int quantity;
    private int alternation;

    public DonutChartData(String name, int quantity, int alternation) {
        super();
        this.name = name;
        this.quantity = quantity;
        this.alternation = alternation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAlternation() {
        return alternation;
    }

    public void setAlternation(int alternation) {
        this.alternation = alternation;
    }

}
