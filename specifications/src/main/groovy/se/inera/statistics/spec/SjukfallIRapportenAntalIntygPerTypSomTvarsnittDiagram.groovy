package se.inera.statistics.spec

import se.inera.statistics.spec.SimpleDetailsReport

class SjukfallIRapportenAntalIntygPerTypSomTvarsnittDiagram extends SimpleDetailsReport {

    String grupp;

    @Override
    public void doExecute() {
        def report = getReportIntygstypTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return grupp
    }

}
