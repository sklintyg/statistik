/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
