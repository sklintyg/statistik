package se.inera.statistics.spec

class SingleLineReport extends Rapport {

    String år;
    String månad;

    void executeTabell(report) {
        def row = report.tableData.rows.find { currentRow -> currentRow.name == (månad + " " + år) }
        kvinnor = row.data[1]
        män = row.data[2]
    }

    Object getReportSjukfallTotalt() {
        if (inloggad) {
            return reportsUtil.getReportAntalIntygInloggad(inloggadSom);
        }
        return reportsUtil.getReportAntalIntyg();
    }

    Object getReportLangaSjukfall() {
        if (inloggad) {
            return reportsUtil.getReportLangaSjukfallInloggad(inloggadSom);
        }
        throw new RuntimeException("Report -Långa sjukfall- is not available on national level");
    }
}
