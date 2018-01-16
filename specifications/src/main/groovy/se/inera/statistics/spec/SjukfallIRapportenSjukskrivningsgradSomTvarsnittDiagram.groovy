package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningsgradSomTvarsnittDiagram extends SimpleDetailsReport {

    String sjukskrivningsgrad;

    @Override
    public void doExecute() {
        def report = getReportSjukskrivningsgradTvarsnitt()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return sjukskrivningsgrad
    }

}
