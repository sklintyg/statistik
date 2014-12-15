package se.inera.statistics.spec

class SjukfallIRapportenLan extends LanRapport {

    public void doExecute() {
        def report = getReport()
        executeTabell(report)
    }

}
