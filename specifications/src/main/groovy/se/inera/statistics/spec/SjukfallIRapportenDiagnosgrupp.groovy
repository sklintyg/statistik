package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgrupp extends DiagnosRapport {

    public void doExecute() {
        def report = getReportDiagnosgrupp()
        executeTabell(report)
    }

}
