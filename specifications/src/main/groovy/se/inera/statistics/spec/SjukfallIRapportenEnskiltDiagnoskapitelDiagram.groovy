package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitelDiagram extends DualSexTimeSeriesReport {

    String valtDiagnoskapitel

    public void doExecute() {
        def report = getReportEnskiltDiagnoskapitel(valtDiagnoskapitel)
        executeDiagram(report)
    }

}
