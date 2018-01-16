package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenTotaltSomTvarsnittDiagram extends SimpleDetailsReport {

    def ämne

    @Override
    public void doExecute() {
        def report = getReportMeddelandenTotaltTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return ämne
    }

}
