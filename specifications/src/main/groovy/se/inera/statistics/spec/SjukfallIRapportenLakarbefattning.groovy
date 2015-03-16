package se.inera.statistics.spec

class SjukfallIRapportenLakarbefattning extends SimpleDetailsReport {

    def läkarbefattning

    @Override
    public void doExecute() {
        def report = getReportLakarBefattning()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return läkarbefattning
    }

}
