package se.inera.statistics.spec

class SjukfallIRapportenAldersgruppDiagram extends SimpleDetailsReport {

    def åldersgrupp

    @Override
    public void doExecute() {
        def report = getReportAldersgrupp()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return åldersgrupp
    }

}
