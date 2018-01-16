package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningslangdDiagram extends SimpleDetailsReport {

    def sjukskrivningslängd

    @Override
    public void doExecute() {
        def report = getReportSjukskrivningslangd()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return sjukskrivningslängd
    }

}
