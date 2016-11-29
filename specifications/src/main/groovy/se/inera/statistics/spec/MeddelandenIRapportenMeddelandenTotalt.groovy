package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenTotalt extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportMeddelandenTotalt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
