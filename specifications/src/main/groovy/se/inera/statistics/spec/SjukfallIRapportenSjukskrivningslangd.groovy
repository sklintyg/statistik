package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningslangd extends SimpleDetailsReport {

    def sjukskrivningslängd

    @Override
    public void doExecute() {
        def report = getReportSjukskrivningslangd()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return sjukskrivningslängd
    }

}
