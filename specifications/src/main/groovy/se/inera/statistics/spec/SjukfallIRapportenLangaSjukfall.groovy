package se.inera.statistics.spec

class SjukfallIRapportenLangaSjukfall extends SingleLineReport {

    public void execute() {
        def report = getReportLangaSjukfall()
        executeTabell(report)
    }

}
