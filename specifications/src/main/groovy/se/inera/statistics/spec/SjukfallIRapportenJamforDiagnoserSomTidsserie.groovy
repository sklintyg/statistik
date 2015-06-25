package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType

class SjukfallIRapportenJamforDiagnoserSomTidsserie extends DualSexTimeSeriesReport {

    def valdaDiagnoskategorier = ""

    public void doExecute() {
        def report = getReportJamforDiagnoserSomTidsserie(valdaDiagnoskategorier)
        executeTabell(report)
    }

    void setDiagnoskategori(String diagnoskategori) {
        super.setGrupp(diagnoskategori)
    }

    void setValdaDiagnoskategorier(String kategoriString) {
        if (kategoriString != null && !kategoriString.trim().isEmpty()) {
            this.valdaDiagnoskategorier = kategoriString.split(",")*.trim().collect{
                String.valueOf(Icd10.icd10ToInt(it, Icd10RangeType.KATEGORI))
            }
        }
    }

}
