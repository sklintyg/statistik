package se.inera.statistics.spec

abstract class DiagnosRapport extends Rapport {

    String år
    String månad
    String grupp

    public void executeDiagram(report) {
        def index = report.maleChart.categories.findIndexOf { item -> item == (månad + " " + år) }
        def male = report.maleChart.series.find { item -> item.name.contains(grupp) }
        män = male.data[index]
        def female = report.femaleChart.series.find { item -> item.name.contains(grupp) }
        kvinnor = female.data[index]
    }

    public void executeTabell(report) {
        def index = report.tableData.headers[0].findIndexOf { item -> item.text.contains(grupp.toUpperCase()) }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år)  }
        def womenIndex = ((index - 2) * 2) + 1
        def menIndex = womenIndex + 1
        totalt = row.data[0]
        kvinnor = row.data[womenIndex]
        män = row.data[menIndex]
    }

    def getReportEnskiltDiagnoskapitel(kapitel) {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(kapitel, inloggadSom);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(kapitel);
    }

    def getReportDiagnosgrupp() {
        if (inloggad) {
            return reportsUtil.getReportDiagnosgruppInloggad(inloggadSom);
        }
        return reportsUtil.getReportDiagnosgrupp();
    }

    def getReportSjukskrivningsgrad() {
        if (inloggad) {
            return reportsUtil.getReportSjukskrivningsgradInloggad(inloggadSom);
        }
        return reportsUtil.getReportSjukskrivningsgrad();
    }

}
