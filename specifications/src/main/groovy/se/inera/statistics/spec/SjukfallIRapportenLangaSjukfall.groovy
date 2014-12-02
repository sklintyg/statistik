package se.inera.statistics.spec

class SjukfallIRapportenLangaSjukfall extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportLangaSjukfall()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
