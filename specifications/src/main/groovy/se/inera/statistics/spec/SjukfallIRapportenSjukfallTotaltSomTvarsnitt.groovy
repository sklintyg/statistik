package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotaltSomTvarsnitt extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotaltTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
