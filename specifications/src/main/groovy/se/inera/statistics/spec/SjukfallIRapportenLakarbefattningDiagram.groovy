package se.inera.statistics.spec

class SjukfallIRapportenLakarbefattningDiagram extends SimpleDetailsReport {

    def läkarbefattning

    @Override
    public void doExecute() {
        def report = getReportLakarBefattning()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return läkarbefattning
    }

}
