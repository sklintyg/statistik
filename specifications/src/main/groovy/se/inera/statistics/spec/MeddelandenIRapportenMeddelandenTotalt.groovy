package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenTotalt extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportMeddelandenTotalt()
        executeTabell(report)
    }

}
