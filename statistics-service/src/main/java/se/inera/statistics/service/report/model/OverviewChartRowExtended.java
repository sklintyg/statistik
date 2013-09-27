package se.inera.statistics.service.report.model;

public class OverviewChartRowExtended extends OverviewChartRow {

    private final int alternation;

    public OverviewChartRowExtended(String name, int quantity, int alternation) {
        super(name, quantity);
        this.alternation = alternation;
    }

    public int getAlternation() {
        return alternation;
    }

    
}
