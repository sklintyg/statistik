package se.inera.statistics.spec

import se.inera.statistics.spec.DualSexTimeSeriesReport

class SjukfallIRapportenAntalIntygPerManadOchTyp extends DualSexTimeSeriesReport {

    public void doExecute() {
        def report = getReportIntygstyp()
        executeTabell(report)
    }

}
