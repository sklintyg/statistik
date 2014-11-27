package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitelDiagram extends DiagnosRapport {

    String valtDiagnoskapitel

    public void execute() {
        def report = getReportEnskiltDiagnoskapitel(valtDiagnoskapitel)
        executeDiagram(report)
    }

}
