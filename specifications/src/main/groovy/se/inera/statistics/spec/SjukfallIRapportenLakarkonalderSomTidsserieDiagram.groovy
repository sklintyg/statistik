package se.inera.statistics.spec

class SjukfallIRapportenLakarkonalderSomTidsserieDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportLakarkonalderSomTidsserie()
        executeDiagram(report)
    }

    void setLäkargrupp(String läkargrupp) {
        super.setGrupp(läkargrupp)
    }

}
