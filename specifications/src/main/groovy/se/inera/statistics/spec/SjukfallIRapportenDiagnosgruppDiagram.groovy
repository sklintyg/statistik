package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgruppDiagram extends DiagnosRapport {

    public void doExecute() {
        def report = getReportDiagnosgrupp()
        executeDiagram(report)
    }

}
