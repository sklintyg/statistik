package se.inera.statistics.spec

class SjukfallIRapportenLangaSjukfallDiagram extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportLangaSjukfall()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
