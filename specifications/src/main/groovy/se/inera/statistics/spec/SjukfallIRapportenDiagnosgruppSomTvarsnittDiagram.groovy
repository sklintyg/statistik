package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgruppSomTvarsnittDiagram extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportDiagnosgruppTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
