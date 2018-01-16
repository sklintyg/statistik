package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallTotaltDiagram extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotaltLandsting()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
