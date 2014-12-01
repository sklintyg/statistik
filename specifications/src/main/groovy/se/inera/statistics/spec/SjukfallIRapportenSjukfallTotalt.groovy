package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotalt extends SingleLineReport {

    public void execute() {
        def report = getReportSjukfallTotalt()
        executeTabell(report)
    }

}
