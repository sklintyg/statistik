package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotalt extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotalt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
