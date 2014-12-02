package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningslangdPagaende extends SimpleDetailsReport {

    def sjukskrivningslängd

    @Override
    public void doExecute() {
        def report = getReportSjukskrivningslangdPagaende()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return sjukskrivningslängd
    }

}
