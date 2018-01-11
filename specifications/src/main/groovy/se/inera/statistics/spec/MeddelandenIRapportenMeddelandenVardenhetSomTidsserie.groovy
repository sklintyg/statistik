package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenVardenhetSomTidsserie extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportMeddelandenVardenhet()
        executeTabell(report)
    }

}
