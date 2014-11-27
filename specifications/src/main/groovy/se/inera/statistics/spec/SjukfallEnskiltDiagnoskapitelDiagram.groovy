package se.inera.statistics.spec

class SjukfallEnskiltDiagnoskapitelDiagram extends Rapport {

    String år
    String månad
    String valtDiagnoskapitel
    String grupp

    public void execute() {
        def report = getReport()
        def index = report.maleChart.categories.findIndexOf { item -> item == (månad + " " + år) }
        def male = report.maleChart.series.find { item -> item.name.contains(grupp) }
        män = male.data[index]
        def female = report.femaleChart.series.find { item -> item.name.contains(grupp) }
        kvinnor = female.data[index]
    }

    private Object getReport() {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(valtDiagnoskapitel);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(valtDiagnoskapitel);
    }

}
