package se.inera.statistics.spec

class SjukfallIRapportenDifferentieratIntygandeSomTvarsnitt extends SimpleDetailsReport {

    String typ;

    @Override
    public void doExecute() {
        def report = getReportDifferentieratIntygandeTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return typ
    }

}
