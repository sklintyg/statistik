package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerEnhetSomTidsserie extends DiagnosRapport {

    public void doExecute() {
        def report = getReportSjukfallPerEnhetSomTidsserie()
        executeTabell(report)
    }

    void setVårdenhet(String vårdenhet) {
        super.setGrupp(vårdenhet)
    }

}
