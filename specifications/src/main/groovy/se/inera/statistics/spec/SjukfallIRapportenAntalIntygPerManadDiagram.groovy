package se.inera.statistics.spec

class SjukfallIRapportenAntalIntygPerManadDiagram extends SimpleDetailsReport {

    @Override
    public void doExecute() {
        def report = getReportAntalIntygPerManad()
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return månad + " " + år
    }

}
