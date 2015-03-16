package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitelDiagram extends DiagnosRapport {

    String valtDiagnoskapitel

    public void doExecute() {
        def report = getReportEnskiltDiagnoskapitel(valtDiagnoskapitel)
        executeDiagram(report)
    }

}
