package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenVardenhetSomTvarsnittLandsting extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportMeddelandenVardenhetLandsting()
        executeTabell(report)
    }

}
