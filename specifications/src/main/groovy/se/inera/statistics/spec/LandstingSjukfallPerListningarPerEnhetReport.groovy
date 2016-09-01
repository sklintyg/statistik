package se.inera.statistics.spec

abstract class LandstingSjukfallPerListningarPerEnhetReport extends Rapport {

    def vårdenhet
    def antalSjukfall
    def antalListningar
    def antalSjukfallPerTusenListningar

    def antalSjukfall() {
        return antalSjukfall
    }

    def antalListningar() {
        return antalListningar
    }

    def antalSjukfallPerTusenListningar() {
        return antalSjukfallPerTusenListningar
    }

    public void reset() {
        super.reset();
        antalSjukfall = -1
        antalListningar = -1
        antalSjukfallPerTusenListningar = -1
    }

    public void executeDiagram(report) {
        executeWithReport(report)
        def index = report.chartData.categories.findIndexOf { item ->
            println("item:" + item + " ; matcher: " + vårdenhet)
            item.name.contains(vårdenhet)
        }
        if (index >= 0) {
            markerad = report.chartData.categories[index].marked
            def series = report.chartData.series[0]
            antalSjukfallPerTusenListningar = series.data[index]
        }
    }

    void executeTabell(report) {
        executeWithReport(report)
        def row = report.tableData.rows.find { currentRow ->
            currentRow.name.contains(vårdenhet) }
        if (row == null) {
            antalSjukfall = -1
            antalListningar = -1
            antalSjukfallPerTusenListningar = -1
        } else {
            antalSjukfall = row.data[0]
            antalListningar = row.data[1]
            antalSjukfallPerTusenListningar = row.data[2]
            markerad = row.marked
        }
    }

    def getReportSjukfallPerListningarPerEnhetLandsting() {
        if (inloggad) {
            return reportsUtil.getReportSjukfallPerListningarPerEnhetLandsting(vg, filter);
        }
        throw new RuntimeException("Report -Landsting Sjukfall per listningar per enhet- is not available on national level");
    }

}
