package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerLakareSomTidsserieDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportSjukfallPerLakareSomTidsserie()
        executeDiagram(report)
    }

    void setLäkare(String läkare) {
        super.setGrupp(läkare)
    }

}
