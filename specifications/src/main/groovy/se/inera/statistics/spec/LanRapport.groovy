package se.inera.statistics.spec

abstract class LanRapport extends Rapport {

    String län
    def antalInvånare
    def sjukfallPer1000Invånare

    def antalInvånare() {
        return antalInvånare
    }

    def sjukfallPer1000Invånare() {
        return sjukfallPer1000Invånare
    }

    public void executeDiagram(report) {
        def index = report.chartData.categories.findIndexOf { item -> item.name.equals(län) }
        markerad = report.chartData.categories[index].marked
        sjukfallPer1000Invånare = index < 0 ? -1 : report.chartData.series[0].data[index]
    }

    public void executeTabell(report) {
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (län)  }
        totalt = row == null ? -1 : row.data[0]
        antalInvånare = row == null ? -1 : row.data[1]
        sjukfallPer1000Invånare = row == null ? -1 : row.data[2]
        markerad = row == null ? false : row.marked
    }

    def getReport() {
        if (inloggad) {
            throw new RuntimeException("Report -Län- is not available on logged in level");
        }
        return reportsUtil.getReportCasesPerCounty();
    }

}
