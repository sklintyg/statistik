package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerLakare extends SimpleDetailsReport {

    def läkare

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerLakare()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return läkare
    }

}
