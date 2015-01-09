package se.inera.statistics.spec

class SjukfallIRapportenJamforDiagnoser extends SimpleDetailsReport {

    def valdaDiagnoskategorier = ""
    def diagnoskategori

    @Override
    public void doExecute() {
        def report = getReportJamforDiagnoser(valdaDiagnoskategorier)
        executeTabell(report)
    }

    @Override
    def getRowNameMatcher() {
        return diagnoskategori
    }


}
