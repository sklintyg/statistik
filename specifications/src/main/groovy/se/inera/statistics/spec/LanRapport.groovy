package se.inera.statistics.spec

abstract class LanRapport extends Rapport {

    String period
    String län

    public void executeDiagram(report) {
        def index = report.chartData.categories.findIndexOf { item -> item.name.equals(län) }
        markerad = report.chartData.categories[index].marked
        def male = report.chartData.series.find { item -> item.name.contains(period + " män") }
        män = male.data[index]
        def female = report.chartData.series.find { item -> item.name.contains(period + " kvinnor") }
        kvinnor = female.data[index]
    }

    public void executeTabell(report) {
        def index = report.tableData.headers[0].findIndexOf { item -> item.text.contains(period) }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (län)  }
        def totalIndex = ((index - 1) * 3)
        def womenIndex = totalIndex + 1
        def menIndex = womenIndex + 1
        totalt = row.data[totalIndex]
        kvinnor = row.data[womenIndex]
        män = row.data[menIndex]
        markerad = row.marked
    }

    def getReport() {
        if (inloggad) {
            throw new RuntimeException("Report -Län- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerCounty();
    }

}
