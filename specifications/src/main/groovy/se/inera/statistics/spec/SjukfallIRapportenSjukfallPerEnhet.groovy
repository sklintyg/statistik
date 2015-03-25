package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerEnhet extends SimpleDetailsReport {

    String vårdenhet

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerEnhet()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return vårdenhet
    }

}
