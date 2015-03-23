package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgruppDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportDiagnosgrupp()
        executeDiagram(report)
    }

}
