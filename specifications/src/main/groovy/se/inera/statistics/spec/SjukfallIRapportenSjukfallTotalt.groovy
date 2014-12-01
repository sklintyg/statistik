package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotalt extends SimpleDetailsReport {

    public void doExecute() {
        def report = getReportSjukfallTotalt()
        executeTabell(report)
    }

}
