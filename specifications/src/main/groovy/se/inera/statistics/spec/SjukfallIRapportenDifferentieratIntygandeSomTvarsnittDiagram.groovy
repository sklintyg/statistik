package se.inera.statistics.spec

class SjukfallIRapportenDifferentieratIntygandeSomTvarsnittDiagram extends SimpleDetailsReport {

    String typ;

    @Override
    public void doExecute() {
        def report = getReportDifferentieratIntygandeTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return typ
    }

}
