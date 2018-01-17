package se.inera.statistics.spec

class IntygIRapportenIntygTotaltSomTvarsnitt extends SimpleDetailsReport {

    def grupp

    @Override
    public void doExecute() {
        def report = getReportIntygTotaltTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
