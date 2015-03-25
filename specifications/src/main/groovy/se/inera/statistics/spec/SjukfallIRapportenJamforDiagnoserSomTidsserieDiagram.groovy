package se.inera.statistics.spec

import se.inera.statistics.service.report.util.Icd10
import se.inera.statistics.service.report.util.Icd10RangeType

class SjukfallIRapportenJamforDiagnoserSomTidsserieDiagram extends DualSexTimeSeriesReport {

    def valdaDiagnoskategorier = ""

    public void doExecute() {
        def report = getReportJamforDiagnoserSomTidsserie(valdaDiagnoskategorier)
        executeDiagram(report)
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
