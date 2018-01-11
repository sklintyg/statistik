package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenVardenhetSomTidsserieDiagram extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportMeddelandenVardenhet()
        executeDiagram(report)
    }

}
