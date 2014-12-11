package se.inera.statistics.spec

class SjukfallIRapportenAndelSjukfallPerKon extends SimpleDetailsReport {

    def län

    @Override
    public void doExecute() {
        def report = getReportAndelSjukfallPerKon()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return län
    }

}
