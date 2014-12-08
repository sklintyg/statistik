package se.inera.statistics.spec

class SjukfallIRapportenAldersgruppPagaendeDiagram extends SimpleDetailsReport {

    def åldersgrupp

    @Override
    public void doExecute() {
        def report = getReportAldersgruppPagaende()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return åldersgrupp
    }

}
