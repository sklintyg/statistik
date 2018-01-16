package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitel extends DualSexTimeSeriesReport {

    String valtDiagnoskapitel

    public void doExecute() {
        def report = getReportEnskiltDiagnoskapitel(valtDiagnoskapitel)
        executeTabell(report)
    }

}
