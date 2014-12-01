package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgrupp extends DiagnosRapport {

    public void execute() {
        def report = getReportDiagnosgrupp()
        executeTabell(report)
    }

}
