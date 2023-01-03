/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

import se.inera.statistics.web.service.dto.FilterData

abstract class DualSexTimeSeriesReport extends Rapport {

    String år
    String månad
    String grupp
    String grupperLista

    /**
     * List of groups ordered as in the response. Makes it possible to validate that all groups are returned and in the correct order.
     */
    def grupperLista() {
        return grupperLista
    }

    public void executeDiagram(report) {
        executeWithReport(report)
        grupperLista = report.maleChart.series.collect { item -> item.name }.join(",")

        def index = report.maleChart.categories.findIndexOf { item -> item.name == getKategoriName() }
        markerad = index < 0 ? null : report.maleChart.categories[index].marked
        def male = report.maleChart.series.find { item ->
            println("Chart - item" + item + " : grupp: " + grupp + " : index: " + index)
            item.name.contains(grupp)
        }
        män = index < 0 || male == null ? -1 : male.data[index]
        def female = report.femaleChart.series.find { item -> item.name.contains(grupp) }
        kvinnor = index < 0 || female == null ? -1 : female.data[index]
        färg = female == null ? null : female.color
    }

    protected String getKategoriName() {
        månad + " " + år
    }

    public void executeTabell(report) {
        executeWithReport(report)
        grupperLista = report.tableData.headers[0].collect { item -> item.text }.join(",")

        def index = report.tableData.headers[0].findIndexOf { item ->
            println("item" + item + " : grupp: " + grupp)
            item.text != null && item.text.contains(grupp)
        }
        def row = report.tableData.rows.find { currentRow -> currentRow.name.contains(getKategoriName())  }
        if (index < 0 || row == null) {
            totalt = -1
            gruppTotalt = -1;
            kvinnor = -1
            män = -1
        } else {
            def totalIndex = ((index - 2) * 3) + 1
            def womenIndex = totalIndex + 1
            def menIndex = womenIndex + 1
            totalt = row.data[0]
            gruppTotalt = row.data[totalIndex];
            kvinnor = row.data[womenIndex]
            män = row.data[menIndex]
            markerad = row.marked
        }
    }

    def getReportEnskiltDiagnoskapitel(kapitel) {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(vg, kapitel, filter);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(kapitel);
    }

    def getReportDiagnosgrupp() {
        if (inloggad) {
            return reportsUtil.getReportDiagnosgruppInloggad(vg, filter);
        }
        return reportsUtil.getReportDiagnosgrupp();
    }

    def getReportIntygstyp() {
        if (inloggad) {
            return reportsUtil.getReportIntygstypInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Intyg per typ- is not available on national level");
    }

    def getReportIntygPerSjukfallTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportIntygPerSjukfallTidsserieInloggad(vg, filter);
        }
        return reportsUtil.getReportIntygPerSjukfallTidsserie(vg, filter);
    }

    def getReportSjukskrivningsgrad() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningsgradInloggad(vg, filter);
        }
        return reportsUtil.getReportSjukskrivningsgrad();
    }

    def getReportSjukfallPerEnhetSomTidsserie(vardenhetdepth) {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhetSomTidsserieInloggad(vg, filter, vardenhetdepth);
        }
        throw new RuntimeException("Report -Sjukfall per enhet som tidsserie- is not available on national level");
    }

    def getReportJamforDiagnoserSomTidsserie(diagnoser) {
        def diagnosHash = reportsUtil.getFilterHash(FilterData.createForDxsOnly(diagnoser))
        if (inloggad) {
            return reportsUtil.getReportJamforDiagnoserSomTidsserieInloggad(vg, filter, diagnosHash);
        }
        throw new RuntimeException("Report -Jämför diagnoser som tidsserie- is not available on national level");
    }

    def getReportAldersgruppSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppSomTidsserieInloggad(vg, filter);
        }
        throw new RuntimeException("Report -åldersgrupp som tidsserie- is not available on national level");
    }

    def getReportSjukskrivningslangdSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdSomTidsserieInloggad(vg, filter);
        }
        throw new RuntimeException("Report -sjukskrivningslangd som tidsserie- is not available on national level");
    }

    def getReportLakarBefattningSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportLakarBefattningSomTidsserieInloggad(vg, filter);
        }
        throw new RuntimeException("Report -läkarbefattning som tidsserie- is not available on national level");
    }

    def getReportSjukfallPerLakareSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerLakareSomTidsserieInloggad(vg, filter);
        }
        throw new RuntimeException("Report -sjukfall per läkare som tidsserie- is not available on national level");
    }

    def getReportLakarkonalderSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportLakareAlderOchKonSomTidsserieInloggad(vg, filter);
        }
        throw new RuntimeException("Report -läkareålder och kön som tidsserie- is not available on national level");
    }

    def getReportMeddelandenTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenInloggad(vg, filter)
        }
        return reportsUtil.getReportAntalMeddelanden()
    }

    def getReportMeddelandenTotaltRegion() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenRegion(vg, filter)
        }
        throw new RuntimeException("Report -Meddelanden per ämne region- is not available on national level");
    }

    def getReportMeddelandenVardenhet(vardenhetdepth) {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenVardenhetInloggad(vg, filter, vardenhetdepth)
        }
        return reportsUtil.getReportAntalMeddelanden()
    }

    def getReportMeddelandenLakare() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenLakareInloggad(vg, filter)
        }
        throw new RuntimeException("Report -Meddelanden per ämne per läkare- is not available on national level");
    }

    def getReportMeddelandenVardenhetRegion() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenVardenhetRegion(vg, filter)
        }
        throw new RuntimeException("Report -Meddelanden per ämne region- is not available on national level");
    }

    def getReportMeddelandenVardenhetTvarsnitt(vardenhetdepth) {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenVardenhetTvarsnittInloggad(vg, filter, vardenhetdepth)
        }
        return new RuntimeException("Report -Meddelanden per ämne per enhet som tvärsnitt- is not available on national level");
    }

    def getReportMeddelandenLakareTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenLakareTvarsnittInloggad(vg, filter)
        }
        return new RuntimeException("Report -Meddelanden per ämne per lakare som tvärsnitt- is not available on national level");
    }

    def getReportIntygTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad(vg, filter);
        }
        return reportsUtil.getReportAntalIntyg();
    }

    def getReportIntygTotaltRegion() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygRegion(vg, filter);
        }
        return new RuntimeException("Report -Intyg totalt Region- is not available on national level");
    }

    def getReportAndelKompletteringar() {
        if (inloggad) {
            return reportsUtil.getReportAndelKompletteringarInloggad(vg, filter);
        }
        return reportsUtil.getReportAndelKompletteringar();
    }

    def getReportKompletteringarPerFraga() {
        if (inloggad) {
            return reportsUtil.getReportKompletteringarPerFragaInloggad(vg, filter);
        }
        return new RuntimeException("Report Kompletteringar Per Fraga -- is not available on national level");
    }

    def getReportAndelKompletteringarRegion() {
        if (inloggad) {
            return reportsUtil.getReportAndelKompletteringarRegion(vg, filter);
        }
        return new RuntimeException("Report -Andel kompletteringar region- is not available on national level");
    }

}
