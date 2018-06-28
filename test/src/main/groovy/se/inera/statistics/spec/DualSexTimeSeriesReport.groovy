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
        def row = report.tableData.rows.find { currentRow -> currentRow.name == getKategoriName()  }
        if (index < 0 || row == null) {
            totalt = -1
            könTotalt = -1;
            kvinnor = -1
            män = -1
        } else {
            def totalIndex = ((index - 2) * 3) + 1
            def womenIndex = totalIndex + 1
            def menIndex = womenIndex + 1
            totalt = row.data[0]
            könTotalt = row.data[totalIndex];
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

    def getReportSjukskrivningsgrad() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningsgradInloggad(vg, filter);
        }
        return reportsUtil.getReportSjukskrivningsgrad();
    }

    def getReportSjukfallPerEnhetSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhetSomTidsserieInloggad(vg, filter);
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

    def getReportMeddelandenTotaltLandsting() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenLandsting(vg, filter)
        }
        throw new RuntimeException("Report -Meddelanden per ämne landsting- is not available on national level");
    }

    def getReportMeddelandenVardenhet() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenVardenhetInloggad(vg, filter)
        }
        return reportsUtil.getReportAntalMeddelanden()
    }

    def getReportMeddelandenVardenhetLandsting() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenLandsting(vg, filter)
        }
        throw new RuntimeException("Report -Meddelanden per ämne landsting- is not available on national level");
    }

    def getReportMeddelandenVardenhetTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenVardenhetTvarsnittInloggad(vg, filter)
        }
        return new RuntimeException("Report -Meddelanden per ämne per enhet- is not available on national level");
    }

    def getReportIntygTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad(vg, filter);
        }
        return reportsUtil.getReportAntalIntyg();
    }

    def getReportIntygTotaltLandsting() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygLandsting(vg, filter);
        }
        return new RuntimeException("Report -Intyg totalt Landsting- is not available on national level");
    }

    def getReportAndelKompletteringar() {
        if (inloggad) {
            return reportsUtil.getReportAndelKompletteringarInloggad(vg, filter);
        }
        return reportsUtil.getReportAndelKompletteringar();
    }

    def getReportAndelKompletteringarLandsting() {
        if (inloggad) {
            return reportsUtil.getReportAndelKompletteringarLandsting(vg, filter);
        }
        return new RuntimeException("Report -Andel kompletteringar landsting- is not available on national level");
    }

}
