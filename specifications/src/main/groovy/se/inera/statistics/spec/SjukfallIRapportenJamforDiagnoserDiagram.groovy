package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType

class SjukfallIRapportenJamforDiagnoserDiagram extends SimpleDetailsReport {

    def valdaDiagnoser = []
    def diagnoskategori

    public void reset() {
        super.reset()
        valdaDiagnoser = []
    }

    @Override
    public void doExecute() {
        def report = getReportJamforDiagnoser(valdaDiagnoser)
        executeDiagram(report)
    }

    @Override
    def getRowNameMatcher() {
        return diagnoskategori
    }

    void setValdaDiagnoskategorier(String kategoriString) {
        if (kategoriString != null && !kategoriString.trim().isEmpty()) {
            this.valdaDiagnoser.addAll(kategoriString.split(",")*.trim().collect{
                def code = it.replaceAll("intern", "")
                String.valueOf(Icd10.icd10ToInt(code, Icd10RangeType.KATEGORI) * (it.endsWith("intern") ? -1 : 1))
            })
        }
    }

    void setValdaDiagnoskoder(String kodString) {
        if (kodString != null && !kodString.trim().isEmpty()) {
            this.valdaDiagnoser.addAll(kodString.split(",")*.trim().collect{
                def code = it.replaceAll("intern", "")
                String.valueOf(Icd10.icd10ToInt(code, Icd10RangeType.KOD) * (it.endsWith("intern") ? -1 : 1))
            })
        }
    }

}
