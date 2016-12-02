package se.inera.statistics.spec

class SjukfallIRapportenAntalIntygPerManad extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportAntalIntygPerManad()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
