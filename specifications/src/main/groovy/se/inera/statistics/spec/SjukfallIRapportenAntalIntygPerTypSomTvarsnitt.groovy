package se.inera.statistics.spec

import se.inera.statistics.spec.SimpleDetailsReport

class SjukfallIRapportenAntalIntygPerTypSomTvarsnitt extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportIntygstypTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
