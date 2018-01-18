package se.inera.statistics.spec

class IntygIRapportenIntygTotaltLandsting extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportIntygTotaltLandsting()
        executeTabell(report)
    }

}
