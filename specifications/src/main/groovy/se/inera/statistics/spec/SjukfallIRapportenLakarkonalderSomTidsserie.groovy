package se.inera.statistics.spec

class SjukfallIRapportenLakarkonalderSomTidsserie extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportLakarkonalderSomTidsserie()
        executeTabell(report)
    }

    void setLäkargrupp(String läkargrupp) {
        super.setGrupp(läkargrupp)
    }

}
