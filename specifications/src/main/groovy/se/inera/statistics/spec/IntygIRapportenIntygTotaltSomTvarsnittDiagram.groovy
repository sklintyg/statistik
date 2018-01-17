package se.inera.statistics.spec

class IntygIRapportenIntygTotaltSomTvarsnittDiagram extends SimpleDetailsReport {

    def grupp

    @Override
    public void doExecute() {
        def report = getReportIntygTotaltTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
