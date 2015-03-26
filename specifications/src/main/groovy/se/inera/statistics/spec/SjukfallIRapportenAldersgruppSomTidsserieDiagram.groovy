package se.inera.statistics.spec

class SjukfallIRapportenAldersgruppSomTidsserieDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportAldersgruppSomTidsserie()
        executeDiagram(report)
    }

    void setÅldersgrupp(String åldersgrupp) {
        super.setGrupp(åldersgrupp)
    }

}
