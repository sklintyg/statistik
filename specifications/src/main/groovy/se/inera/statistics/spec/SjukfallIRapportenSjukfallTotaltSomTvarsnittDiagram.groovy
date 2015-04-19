package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotaltSomTvarsnittDiagram extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotaltTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
