package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgrupp extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportDiagnosgrupp()
        executeTabell(report)
    }

}
