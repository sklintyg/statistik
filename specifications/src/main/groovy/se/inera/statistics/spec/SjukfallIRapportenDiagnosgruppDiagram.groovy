package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgruppDiagram extends DiagnosRapport {

    public void execute() {
        def report = getReportDiagnosgrupp()
        executeDiagram(report)
    }

}
