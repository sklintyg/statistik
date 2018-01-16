package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotaltDiagram extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotalt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
