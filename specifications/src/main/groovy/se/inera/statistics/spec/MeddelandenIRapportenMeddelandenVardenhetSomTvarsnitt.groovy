package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenVardenhetSomTvarsnitt extends DualSexTimeSeriesReport {

    def ämne
    def vårdenhet

    @Override
    public void doExecute() {
        def report = getReportMeddelandenVardenhetTvarsnitt()
        executeTabell(report)
    }

    def getRowNameMatcher() {
        return ämne
    }

    @Override
    protected String getKategoriName() {
        vårdenhet
    }

}
