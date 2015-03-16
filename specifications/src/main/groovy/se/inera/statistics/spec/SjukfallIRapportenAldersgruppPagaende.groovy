package se.inera.statistics.spec

class SjukfallIRapportenAldersgruppPagaende extends SimpleDetailsReport {

    def åldersgrupp

    @Override
    public void doExecute() {
        def report = getReportAldersgruppPagaende()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return åldersgrupp
    }

}
