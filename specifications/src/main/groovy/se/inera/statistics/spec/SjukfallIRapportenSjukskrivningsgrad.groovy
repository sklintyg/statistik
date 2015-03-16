package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningsgrad extends DiagnosRapport {

    public void doExecute() {
        def report = getReportSjukskrivningsgrad()
        executeTabell(report)
    }

    public void setSjukskrivningsgrad(sjukskrivningsgrad) {
        grupp = sjukskrivningsgrad
    }

}
