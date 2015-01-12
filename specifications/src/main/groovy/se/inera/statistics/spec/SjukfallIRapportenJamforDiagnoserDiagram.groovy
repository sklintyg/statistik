package se.inera.statistics.spec

class SjukfallIRapportenJamforDiagnoserDiagram extends SimpleDetailsReport {

    def valdaDiagnoskategorier = ""
    def diagnoskategori

    @Override
    public void doExecute() {
        def report = getReportJamforDiagnoser(valdaDiagnoskategorier)
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return diagnoskategori
    }


}
