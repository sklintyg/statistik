/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    void setValdaDiagnoskapitel(String kapitelString) {
        if (kapitelString != null && !kapitelString.trim().isEmpty()) {
            this.valdaDiagnoser.addAll(kapitelString.split(",")*.trim().collect{
                def code = it.replaceAll("intern", "")
                String.valueOf(Icd10.icd10ToInt(code, Icd10RangeType.KAPITEL) * (it.endsWith("intern") ? -1 : 1))
            })
        }
    }

    void setValdaDiagnosavsnitt(String avsnittString) {
        if (avsnittString != null && !avsnittString.trim().isEmpty()) {
            this.valdaDiagnoser.addAll(avsnittString.split(",")*.trim().collect{
                def code = it.replaceAll("intern", "")
                String.valueOf(Icd10.icd10ToInt(code, Icd10RangeType.AVSNITT) * (it.endsWith("intern") ? -1 : 1))
            })
        }
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
