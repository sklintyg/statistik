package se.inera.statistics.spec

class SjukfallIRapportenLandstingSjukfallTotalt extends SimpleDetailsReport {

    String fileUploadDate

    @Override
    public void doExecute() {
        def report = getReportSjukfallTotaltLandsting()
        executeTabell(report)
        fileUploadDate = report["fileUploadDate"]
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

    def fileUploadDate() {
        return fileUploadDate
    }

}
