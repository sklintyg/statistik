package se.inera.statistics.spec

class SjukfallIRapportenSjukfallPerEnhetSomTidssserieDiagram extends DiagnosRapport {

    public void doExecute() {
        def report = getReportSjukfallPerEnhetSomTidsserie()
        executeDiagram(report)
    }

    void setVårdenhet(String vårdenhet) {
        super.setGrupp(vårdenhet)
    }

}
