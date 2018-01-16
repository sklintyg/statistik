package se.inera.statistics.spec

class SjukfallIRapportenAldersgruppSomTidsserie extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportAldersgruppSomTidsserie()
        executeTabell(report)
    }

    void setÅldersgrupp(String åldersgrupp) {
        super.setGrupp(åldersgrupp)
    }

}
