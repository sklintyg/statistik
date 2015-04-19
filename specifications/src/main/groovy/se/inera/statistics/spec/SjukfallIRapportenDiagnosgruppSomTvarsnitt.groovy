package se.inera.statistics.spec

class SjukfallIRapportenDiagnosgruppSomTvarsnitt extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportDiagnosgruppTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
