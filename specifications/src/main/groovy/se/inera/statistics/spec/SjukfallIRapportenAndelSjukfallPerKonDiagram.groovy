package se.inera.statistics.spec

class SjukfallIRapportenAndelSjukfallPerKonDiagram extends SimpleDetailsReport {

    def län

    @Override
    public void doExecute() {
        def report = getReportAndelSjukfallPerKon()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return län
    }

}
