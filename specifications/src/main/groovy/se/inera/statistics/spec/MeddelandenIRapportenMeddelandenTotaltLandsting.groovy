package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenTotaltLandsting extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportMeddelandenTotaltLandsting()
        executeTabell(report)
    }

}
