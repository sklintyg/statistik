package se.inera.statistics.spec

class SjukfallIRapportenDifferentieratIntygande extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportDifferentieratIntygande()
        executeTabell(report)
    }

    public void setTyp(typ) {
        grupp = typ
    }

}
