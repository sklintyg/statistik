package se.inera.statistics.spec

class SjukfallIRapportenSjukfallTotalt extends Rapport {

    String år;
    String månad;

    public void execute() {
        def report = getReport()
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år) }
        kvinnor = row.data[1]
        män = row.data[2]
    }

    private Object getReport() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad(inloggadSom);
        }
        return reportsUtil.getReportAntalIntyg();
    }

}
