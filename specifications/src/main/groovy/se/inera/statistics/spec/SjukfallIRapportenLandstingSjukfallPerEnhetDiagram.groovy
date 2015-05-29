package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallPerEnhetDiagram extends SimpleDetailsReport {

    String vårdenhet

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerEnhetLandsting()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return vårdenhet
    }

}
