package se.inera.statistics.spec

abstract class LanRapport extends Rapport {

    String län
    String kolumngrupp

    def antalInvånare() {
        return antalInvånare
    }

    def sjukfallPer1000Invånare() {
        return sjukfallPer1000Invånare
    }

    public void executeDiagram(report) {
        def index = report.chartData.categories.findIndexOf { item -> item.name.equals(län) }
        markerad = report.chartData.categories[index].marked
        totalt = index < 0 ? -1 : report.chartData.series[0].data[index]
        kvinnor = index < 0 ? -1 : report.chartData.series[1].data[index]
        män = index < 0 ? -1 : report.chartData.series[2].data[index]
    }

    public void executeTabell(report) {
        def headerIndex = report.tableData.headers[0].findIndexOf { item ->
            item.text != null && item.text.toLowerCase(Locale.ENGLISH).contains(kolumngrupp.toLowerCase(Locale.ENGLISH))
        }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (län)  }
        def totalIndex = ((headerIndex - 1) * 3)
        def womenIndex = totalIndex + 1
        def menIndex = womenIndex + 1
        totalt = row == null ? -1 : row.data[totalIndex]
        kvinnor = row == null ? -1 : row.data[womenIndex]
        män = row == null ? -1 : row.data[menIndex]
        markerad = row == null ? false : row.marked
    }

    def getReport() {
        if (inloggad) {
            throw new RuntimeException("Report -Län- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerCounty();
    }

}
