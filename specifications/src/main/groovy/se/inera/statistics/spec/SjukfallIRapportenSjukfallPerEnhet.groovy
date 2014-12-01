package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerEnhet extends SimpleDetailsReport {

    String vårdenhet

    public void doExecute() {
        def report = getReportSjukfallPerEnhet()
        executeTabell(report, vårdenhet)
    }

}
