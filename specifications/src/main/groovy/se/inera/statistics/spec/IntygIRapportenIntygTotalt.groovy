package se.inera.statistics.spec

class IntygIRapportenIntygTotalt extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportIntygTotalt()
        executeTabell(report)
    }

}
