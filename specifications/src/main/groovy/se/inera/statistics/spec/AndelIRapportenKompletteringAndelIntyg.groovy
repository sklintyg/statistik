package se.inera.statistics.spec

class AndelIRapportenKompletteringAndelIntyg extends DualSexTimeSeriesReport {

    @Override
    public void doExecute() {
        def report = getReportAndelKompletteringar()
        executeTabell(report)
    }

}
