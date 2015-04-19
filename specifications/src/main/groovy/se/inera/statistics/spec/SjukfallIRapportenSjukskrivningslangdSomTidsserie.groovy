package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningslangdSomTidsserie extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportSjukskrivningslangdSomTidsserie()
        executeTabell(report)
    }

    void setSjukskrivningslängd(String sjukskrivningslängd) {
        super.setGrupp(sjukskrivningslängd)
    }

}
