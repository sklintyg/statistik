package se.inera.statistics.spec

class SjukfallIRapportenLangaSjukfall extends SimpleDetailsReport {

    public void doExecute() {
        def report = getReportLangaSjukfall()
        executeTabell(report)
    }

}
