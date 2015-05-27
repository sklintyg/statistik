package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallTotalt extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotaltLandsting()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
