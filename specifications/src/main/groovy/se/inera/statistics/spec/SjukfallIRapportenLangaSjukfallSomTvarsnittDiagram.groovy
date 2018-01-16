package se.inera.statistics.spec

class SjukfallIRapportenLangaSjukfallSomTvarsnittDiagram extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportLangaSjukfallTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
