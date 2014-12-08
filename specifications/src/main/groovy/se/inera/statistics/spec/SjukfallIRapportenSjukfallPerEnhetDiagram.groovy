package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerEnhetDiagram extends SimpleDetailsReport {

    String vårdenhet

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerEnhet()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return vårdenhet
    }

}
