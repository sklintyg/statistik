package se.inera.statistics.spec

import se.inera.statistics.spec.DualSexTimeSeriesReport

class SjukfallIRapportenAntalIntygPerManadOchTypDiagram extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportIntygstyp()
        executeDiagram(report)
    }

}
