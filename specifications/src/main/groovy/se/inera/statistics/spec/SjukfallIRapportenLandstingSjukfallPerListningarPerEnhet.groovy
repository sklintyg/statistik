package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallPerListningarPerEnhet extends LandstingSjukfallPerListningarPerEnhetReport {

    String fileUploadDate

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerListningarPerEnhetLandsting()
        executeTabell(report)
        fileUploadDate = report["fileUploadDate"]
    }

    def fileUploadDate() {
        return fileUploadDate
    }

}
