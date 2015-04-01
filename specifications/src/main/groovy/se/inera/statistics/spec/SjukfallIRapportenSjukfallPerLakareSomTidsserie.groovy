package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerLakareSomTidsserie extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportSjukfallPerLakareSomTidsserie()
        executeTabell(report)
    }

    void setLäkare(String läkare) {
        super.setGrupp(läkare)
    }

}
