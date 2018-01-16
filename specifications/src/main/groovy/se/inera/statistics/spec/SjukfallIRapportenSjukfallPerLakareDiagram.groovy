package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerLakareDiagram extends SimpleDetailsReport {

    def läkare

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerLakare()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return läkare
    }

}
