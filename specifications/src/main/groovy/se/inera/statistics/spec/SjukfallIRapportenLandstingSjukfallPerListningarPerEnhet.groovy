package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallPerListningarPerEnhet extends LandstingSjukfallPerListningarPerEnhetReport {

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerListningarPerEnhetLandsting()
        executeTabell(report)
    }

}
