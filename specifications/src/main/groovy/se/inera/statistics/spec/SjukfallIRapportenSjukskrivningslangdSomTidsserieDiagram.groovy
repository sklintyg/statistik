package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningslangdSomTidsserieDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportSjukskrivningslangdSomTidsserie()
        executeDiagram(report)
    }

    void setSjukskrivningslängd(String sjukskrivningslängd) {
        super.setGrupp(sjukskrivningslängd)
    }

}
