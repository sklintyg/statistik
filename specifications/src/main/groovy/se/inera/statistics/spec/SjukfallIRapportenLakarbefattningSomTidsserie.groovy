package se.inera.statistics.spec

class SjukfallIRapportenLakarbefattningSomTidsserie extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportLakarBefattningSomTidsserie()
        executeTabell(report)
    }

    void setLäkarbefattning(String läkarbefattning) {
        super.setGrupp(läkarbefattning)
    }

}
