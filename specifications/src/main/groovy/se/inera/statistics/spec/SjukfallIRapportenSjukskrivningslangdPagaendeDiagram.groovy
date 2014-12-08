package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningslangdPagaendeDiagram extends SimpleDetailsReport {

    def sjukskrivningslängd

    @Override
    public void doExecute() {
        def report = getReportSjukskrivningslangdPagaende()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return sjukskrivningslängd
    }

}
