package se.inera.statistics.spec

class SjukfallIRapportenLakarbefattningSomTidsserieDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportLakarBefattningSomTidsserie()
        executeDiagram(report)
    }

    void setLäkarbefattning(String läkarbefattning) {
        super.setGrupp(läkarbefattning)
    }

}
