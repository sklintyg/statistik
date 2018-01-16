package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallPerListningarPerEnhetDiagram extends LandstingSjukfallPerListningarPerEnhetReport {

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerListningarPerEnhetLandsting()
        executeDiagram(report)
    }

}
