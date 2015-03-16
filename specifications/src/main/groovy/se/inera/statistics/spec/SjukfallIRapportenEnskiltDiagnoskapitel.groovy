package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitel extends DiagnosRapport {

    String valtDiagnoskapitel

    public void doExecute() {
        def report = getReportEnskiltDiagnoskapitel(valtDiagnoskapitel)
        executeTabell(report)
    }

}
