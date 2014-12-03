package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningsgradDiagram extends DiagnosRapport {

    public void doExecute() {
        def report = getReportSjukskrivningsgrad()
        executeDiagram(report)
    }

    public void setSjukskrivningsgrad(sjukskrivningsgrad) {
        grupp = sjukskrivningsgrad
    }

}
