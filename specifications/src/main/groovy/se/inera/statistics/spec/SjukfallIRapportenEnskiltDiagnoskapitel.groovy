package se.inera.statistics.spec

class SjukfallIRapportenEnskiltDiagnoskapitel extends Rapport {

    String år
    String månad
    String valtDiagnoskapitel
    String grupp

    public void execute() {
        def report = getReport()
        def index = report.tableData.headers[0].findIndexOf { item -> item.text.contains(grupp.toUpperCase()) }
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år)  }
        def womenIndex = ((index - 2) * 2) + 1
        def menIndex = womenIndex + 1
        kvinnor = row.data[womenIndex]
        män = row.data[menIndex]
    }

    private Object getReport() {
        if (inloggad) {
            return reportsUtil.getReportEnskiltDiagnoskapitelInloggad(valtDiagnoskapitel);
        }
        return reportsUtil.getReportEnskiltDiagnoskapitel(valtDiagnoskapitel);
    }

}
