/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import se.inera.statistics.web.service.FilterData

abstract class SimpleDetailsReport extends Rapport {

    String år;
    String månad;

    public void executeDiagram(report) {
        executeWithReport(report)
        def categoryNameMatcher = getRowNameMatcher();
        def index = report.chartData.categories.findIndexOf { item ->
            println("item:" + item + " ; matcher: " + categoryNameMatcher)
            item.name.contains(categoryNameMatcher)
        }
        markerad = index < 0 ? null : report.chartData.categories[index].marked
        def male = report.chartData.series.find { item -> "MALE".equals(item.sex) }
        män = index < 0 ? -1 : male.data[index]
        def female = report.chartData.series.find { item -> "FEMALE".equals(item.sex) }
        kvinnor = index < 0 ? -1 : female.data[index]
        def total = report.chartData.series.find { item -> item.sex == null }
        totalt = index < 0 ? -1 : (total != null ? total.data[index] : -1)
    }

    abstract def getRowNameMatcher()

    void executeTabell(report) {
        executeWithReport(report)
        def rowNameMatcher = getRowNameMatcher();
        def row = report.tableData.rows.find { currentRow ->
            currentRow.name.contains(rowNameMatcher) }
        if (row == null) {
            totalt = -1
            kvinnor = -1
            män = -1
        } else {
            totalt = row.data[0]
            kvinnor = row.data[1]
            män = row.data[2]
            markerad = row.marked
        }
    }

    def getReportAntalIntygPerManad() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygPerManad(vg, filter);
        }
        throw new RuntimeException("Report -AntalIntygPerManad- is not available on national level");
    }

    def getReportSjukfallTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalSjukfallInloggad(vg, filter);
        }
        return reportsUtil.getReportAntalSjukfall();
    }

    def getReportLangaSjukfall() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Långa sjukfall- is not available on national level");
    }

    def getReportSjukfallPerEnhet() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhet(vg, filter);
        }
        throw new RuntimeException("Report -Sjukfall per enhet- is not available on national level");
    }

    def getReportAldersgrupp() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppInloggad(vg, filter);
        }
        return reportsUtil.getReportAldersgrupp();
    }

    def getReportJamforDiagnoser(diagnoser) {
        def diagnosHash = reportsUtil.getFilterHash(FilterData.createForDxsOnly(diagnoser))
        if (inloggad) {
            return reportsUtil.getReportJamforDiagnoserInloggad(vg, filter, diagnosHash);
        }
        throw new RuntimeException("Report -Jämför diagnoser- is not available on national level");
    }

    def getReportSjukskrivningslangd() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdInloggad(vg, filter);
        }
        return reportsUtil.getReportSjukskrivningslangd();
    }

    def getReportLakareAlderOchKon() {
        if (inloggad) {
            return reportsUtil.getReportLakareAlderOchKonInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Läkare ålder och kön- is not available on national level");
    }

    def getReportLakarBefattning() {
        if (inloggad) {
            return reportsUtil.getReportLakarBefattningInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Läkarbefattning- is not available on national level");
    }

    def getReportSjukfallPerLakare() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerLakareInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Sjukfall per läkare- is not available on national level");
    }

    def getReportAndelSjukfallPerKon() {
        if (inloggad) {
            throw new RuntimeException("Report -Andel sjukfall per kön- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerSex();
    }

    def getReportEnskiltDiagnoskapitel(kapitel) {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(vg, kapitel, filter);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(kapitel);
    }

    def getReportSjukfallTotaltTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalSjukfallSomTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Sjukfall totalt som tvärsnitt- is not available on national level");
    }

    def getReportLangaSjukfallTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallSomTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Långa Sjukfall som tvärsnitt- is not available on national level");
    }

    def getReportSjukskrivningsgradTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningsgradSomTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Sjukskrivningsgrad som tvärsnitt- is not available on national level");
    }

    def getReportDiagnosgruppTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportDiagnosgruppSomTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Diagnosgrupp som tvärsnitt- is not available on national level");
    }

    def getReportIntygstypTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportIntygstypTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Intygstyp som tvärsnitt- is not available on national level");
    }

    def getReportUnderdiagnosgruppTvarsnitt(String valdUnderdiagnos) {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelSomTvarsnittInloggad(vg, valdUnderdiagnos, filter);
        }
        throw new RuntimeException("Report -Enskilt diagnoskapitel som tvärsnitt- is not available on national level");
    }

    def getReportSjukfallTotaltLandsting() {
        if (inloggad) {
            return reportsUtil.getReportAntalSjukfallLandstingInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Landsting Sjukfall totalt- is not available on national level");
    }

    def getReportSjukfallPerEnhetLandsting() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhetLandsting(vg, filter);
        }
        throw new RuntimeException("Report -Landsting Sjukfall per enhet- is not available on national level");
    }

    def getReportMeddelandenTotaltTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenTvarsnittInloggad(vg, filter)
        }
        throw new RuntimeException("Report -Meddelanden per ämne som tvärsnitt- is not available on national level");
    }

    def getReportIntygTotaltTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Intyg totalt som tvärsnitt- is not available on national level");
    }

    def getReportAndelKompletteringarTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAndelKompletteringarTvarsnittInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Andel kompletterade intyg som tvärsnitt- is not available on national level");
    }

}
