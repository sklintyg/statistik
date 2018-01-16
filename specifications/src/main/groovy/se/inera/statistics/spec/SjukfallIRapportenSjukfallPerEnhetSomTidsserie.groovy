package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerEnhetSomTidsserie extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportSjukfallPerEnhetSomTidsserie()
        executeTabell(report)
    }

    void setVårdenhet(String vårdenhet) {
        super.setGrupp(vårdenhet)
    }

}
