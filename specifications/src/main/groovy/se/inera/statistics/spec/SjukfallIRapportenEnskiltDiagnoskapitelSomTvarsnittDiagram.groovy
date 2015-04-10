package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitelSomTvarsnittDiagram extends SimpleDetailsReport {

    String grupp;
    String valtDiagnoskapitel

    @Override
    public void doExecute() {
        def report = getReportUnderdiagnosgruppTvarsnitt(valtDiagnoskapitel)
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
