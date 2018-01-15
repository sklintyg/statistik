package se.inera.statistics.spec

class MeddelandenIRapportenMeddelandenVardenhetSomTvarsnittDiagram extends Rapport {

    def ämne
    def vårdenhet

    @Override
    public void doExecute() {
        def report = getReportMeddelandenVardenhetTvarsnitt()
        executeDiagram(report)
    }

    def getRowNameMatcher() {
        return vårdenhet
    }

    public void executeDiagram(report) {
        executeWithReport(report)
        def categoryNameMatcher = getRowNameMatcher();
        def index = report.chartData.categories.findIndexOf { item ->
            println("item:" + item + " ; matcher: " + categoryNameMatcher)
            item.name.toLowerCase().contains(categoryNameMatcher.toLowerCase())
        }
        markerad = index < 0 ? null : report.chartData.categories[index].marked
        def total = report.chartData.series.find { item -> item.name.equalsIgnoreCase(ämne) }
        totalt = index < 0 || total == null ? -1 : total.data[index]
    }

    def getReportMeddelandenVardenhetTvarsnitt() {
        if (inloggad) {
            return reportsUtil.getReportAntalMeddelandenVardenhetTvarsnittInloggad(vg, filter)
        }
        return new RuntimeException("Report -Meddelanden per ämne per enhet- is not available on national level");
    }

}
