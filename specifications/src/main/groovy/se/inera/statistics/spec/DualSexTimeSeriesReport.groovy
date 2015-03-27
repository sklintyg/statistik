package se.inera.statistics.spec

abstract class DualSexTimeSeriesReport extends Rapport {

    String år
    String månad
    String grupp

    public void executeDiagram(report) {
        def index = report.maleChart.categories.findIndexOf { item -> item == (månad + " " + år) }
        def male = report.maleChart.series.find { item ->
            println("item" + item + " : grupp: " + grupp)
            item.name.contains(grupp)
        }
        män = index < 0 || male == null ? -1 : male.data[index]
        def female = report.femaleChart.series.find { item -> item.name.contains(grupp) }
        kvinnor = index < 0 || female == null ? -1 : female.data[index]
    }

    public void executeTabell(report) {
        def index = report.tableData.headers[0].findIndexOf { item -> item.text != null && item.text.contains(grupp) }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år)  }
        if (index < 0 || row == null) {
            totalt = -1
            kvinnor = -1
            män = -1
        } else {
            def womenIndex = ((index - 2) * 2) + 1
            def menIndex = womenIndex + 1
            totalt = row.data[0]
            kvinnor = row.data[womenIndex]
            män = row.data[menIndex]
        }
    }

    def getReportEnskiltDiagnoskapitel(kapitel) {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(kapitel, filter);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(kapitel);
    }

    def getReportDiagnosgrupp() {
        if (inloggad) {
            return reportsUtil.getReportDiagnosgruppInloggad(filter);
        }
        return reportsUtil.getReportDiagnosgrupp();
    }

    def getReportSjukskrivningsgrad() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningsgradInloggad(filter);
        }
        return reportsUtil.getReportSjukskrivningsgrad();
    }

    def getReportSjukfallPerEnhetSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerEnhetSomTidsserieInloggad(filter);
        }
        throw new RuntimeException("Report -Sjukfall per enhet som tidsserie- is not available on national level");
    }

    def getReportJamforDiagnoserSomTidsserie(diagnoser) {
        def diagnosHash = reportsUtil.getFilterHash(null, null, diagnoser)
        if (inloggad) {
            return reportsUtil.getReportJamforDiagnoserSomTidsserieInloggad(filter, diagnosHash);
        }
        throw new RuntimeException("Report -Jämför diagnoser som tidsserie- is not available on national level");
    }

    def getReportAldersgruppSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportAldersgruppSomTidsserieInloggad(filter);
        }
        throw new RuntimeException("Report -åldersgrupp som tidsserie- is not available on national level");
    }

    def getReportSjukskrivningslangdSomTidsserie() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningslangdSomTidsserieInloggad(filter);
        }
        throw new RuntimeException("Report -sjukskrivningslangd som tidsserie- is not available on national level");
    }
}
