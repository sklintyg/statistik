package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenTotaltSomTvarsnitt extends SimpleDetailsReport {

    def ämne

    @Override
    public void doExecute() {
        def report = getReportMeddelandenTotaltTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return ämne
    }

}
