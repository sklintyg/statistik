package se.inera.statistics.spec

class SjukfallIRapportenSjukskrivningsgradSomTvarsnitt extends SimpleDetailsReport {

    String sjukskrivningsgrad;

    @Override
    public void doExecute() {
        def report = getReportSjukskrivningsgradTvarsnitt()
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return sjukskrivningsgrad
    }

}
