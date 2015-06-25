package se.inera.statistics.spec

class SjukfallIRapportenLangaSjukfallSomTvarsnitt extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportLangaSjukfallTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
