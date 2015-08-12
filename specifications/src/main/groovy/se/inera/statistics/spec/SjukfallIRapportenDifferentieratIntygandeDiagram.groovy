package se.inera.statistics.spec

class SjukfallIRapportenDifferentieratIntygandeDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportDifferentieratIntygande()
        executeDiagram(report)
    }

    public void setTyp(typ) {
        grupp = typ
    }

}
