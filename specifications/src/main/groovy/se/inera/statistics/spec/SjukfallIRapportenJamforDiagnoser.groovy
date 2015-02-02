package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType

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

    void setValdaDiagnoskategorier(String kategoriString) {
        if (kategoriString != null && !kategoriString.trim().isEmpty()) {
            this.valdaDiagnoskategorier = kategoriString.split(",")*.trim().collect{
                String.valueOf(Icd10.icd10ToInt(it, Icd10RangeType.KATEGORI))
            }
        }
    }

}
