package se.inera.statistics.spec

class SjukfallIRapportenLakarkonalderDiagram extends SimpleDetailsReport {

    def läkargrupp

    @Override
    public void doExecute() {
        def report = getReportLakareAlderOchKon()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return läkargrupp
    }

}
