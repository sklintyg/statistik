package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningsgradDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportSjukskrivningsgrad()
        executeDiagram(report)
    }

    public void setSjukskrivningsgrad(sjukskrivningsgrad) {
        grupp = sjukskrivningsgrad
    }

}
