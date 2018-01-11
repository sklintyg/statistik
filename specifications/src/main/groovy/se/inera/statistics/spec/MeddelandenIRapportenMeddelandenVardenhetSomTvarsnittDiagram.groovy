package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenVardenhetSomTvarsnittDiagram extends DualSexTimeSeriesReport {

    def ämne
    def vårdenhet

    @Override
    public void doExecute() {
        def report = getReportMeddelandenVardenhetTvarsnitt()
        executeDiagram(report)
    }

    def getRowNameMatcher() {
        return ämne
    }

    @Override
    protected String getKategoriName() {
        vårdenhet
    }

}
