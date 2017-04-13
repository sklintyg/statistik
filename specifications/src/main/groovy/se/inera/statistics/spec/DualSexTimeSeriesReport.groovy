package se.inera.statistics.spec

import se.inera.statistics.web.service.FilterData

abstract class DualSexTimeSeriesReport extends Rapport {

    String år
    String månad
    String grupp

    public void executeDiagram(report) {
        executeWithReport(report)
        def index = report.maleChart.categories.findIndexOf { item -> item.name == (månad + " " + år) }
        markerad = index < 0 ? null : report.maleChart.categories[index].marked
        def male = report.maleChart.series.find { item ->
            println("item" + item + " : grupp: " + grupp)
            item.name.contains(grupp)
        }
        män = index < 0 || male == null ? -1 : male.data[index]
        def female = report.femaleChart.series.find { item -> item.name.contains(grupp) }
        kvinnor = index < 0 || female == null ? -1 : female.data[index]
        färg = female == null ? null : female.color
    }

    public void executeTabell(report) {
        executeWithReport(report)
        def index = report.tableData.headers[0].findIndexOf { item ->
            println("item" + item + " : grupp: " + grupp)
            item.text != null && item.text.contains(grupp)
        }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år)  }
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

    def getReportDifferentieratIntygande() {
        if (inloggad) {
            return reportsUtil.getReportDifferentieratIntygandeSomTidsserieInloggad(vg, filter);
        }
        throw new RuntimeException("Report -Differentierat Intygande- is not available on national level");
    }
}
