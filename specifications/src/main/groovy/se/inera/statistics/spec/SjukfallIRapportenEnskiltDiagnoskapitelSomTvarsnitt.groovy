package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitelSomTvarsnitt extends SimpleDetailsReport {

    String grupp;
    String valtDiagnoskapitel

    @Override
    public void doExecute() {
        def report = getReportUnderdiagnosgruppTvarsnitt(valtDiagnoskapitel)
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
