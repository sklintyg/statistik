package se.inera.statistics.service.report.repository;

public enum RollingLength {
    SINGLE_MONTH(1), QUARTER(3), YEAR(12);
    
    private final int periods;
    
    private RollingLength(int periods) {
        this.periods = periods;
    }
    
    public int getPeriods() {
        return periods;
    }
}
