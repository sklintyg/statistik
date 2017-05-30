package se.inera.statistics.spec

import se.inera.statistics.web.service.ResponseHandler

class SjukfallIRapportenLandstingSjukfallPerEnhet extends SimpleDetailsReport {

    String vårdenhet
    String fileUploadDate

    @Override
    public void doExecute() {
        def report = getReportSjukfallPerEnhetLandsting()
        executeTabell(report)
        fileUploadDate = report["fileUploadDate"]
    }

    @Override
    def getRowNameMatcher() {
        return vårdenhet
    }

    def fileUploadDate() {
        return fileUploadDate
    }

}
