package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallPerEnhet extends SimpleDetailsReport {

    String vårdenhet

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerEnhetLandsting()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return vårdenhet
    }

}
