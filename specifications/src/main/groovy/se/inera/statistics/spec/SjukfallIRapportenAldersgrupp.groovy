package se.inera.statistics.spec

class SjukfallIRapportenAldersgrupp extends SimpleDetailsReport {

    def åldersgrupp

    @Override
    public void doExecute() {
        def report = getReportAldersgrupp()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return åldersgrupp
    }

}
