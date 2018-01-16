package se.inera.statistics.spec

class SjukfallIRapportenLakarkonalder extends SimpleDetailsReport {

    def läkargrupp

    @Override
    public void doExecute() {
        def report = getReportLakareAlderOchKon()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return läkargrupp
    }

}
